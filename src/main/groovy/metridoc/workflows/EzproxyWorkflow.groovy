/*
 * Copyright 2010 Trustees of the University of Pennsylvania Licensed under the
 * Educational Community License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package metridoc.workflows

import groovy.sql.Sql
import groovy.util.logging.Slf4j
import metridoc.dsl.JobBuilder
import metridoc.scripts._TableKeyOperations
import metridoc.scripts._UpdateSchema
import metridoc.utils.SystemUtils

import javax.sql.DataSource

/**
 * The basic workflow loads an ezproxy file into a a loading table, which then gets normalized into an
 * optimal structure.  Before the log is loaded into the database, the destination database is updated
 * for the most recent schemas using liquibase.  The schema diagram is
 * <a href= "http://metridoc.googlecode.com/svn/trunk/documentation/ezproxy/current/ezproxy_model.pdf">here</a>.
 *
 * While processing the log line by line, the width and data are validated.  Any errors are put into a
 * validation error table.  Validation errors are not considered fatal, so the line processing will continue
 * despite errors.  Any other errors are considered fatal and the program will end.
 *
 *
 */
@Slf4j
class EzproxyWorkflow extends Script {
    /**
     * Location of the database that contains the ezproxy <a href= "http://metridoc.googlecode.com/svn/trunk/documentation/ezproxy/current/ezproxy_model.pdf">schema</a>.
     */
    DataSource dataSource
    /**
     * Configuration for the file loader.
     *
     * @see LoadFileIntoTable
     */
    Map loadFileToTableConfig
    /**
     * service to load a file into a staging table
     */
    LoadFileIntoTable loadFileIntoTable
    /**
     * service to update a schema
     */
    def schemaUpdate
    /**
     * service to normalize a loading / staging table into a normalized structure.  See the ezproxy
     * <a href= "http://metridoc.googlecode.com/svn/trunk/documentation/ezproxy/current/ezproxy_model.pdf">schema</a>
     * for information on the default schema structure for this workflow
     */
    SqlNormalizer sqlNormalizer
    /**
     * configuration for the {@link SqlNormalizer} used by this workflow
     */
    Map sqlNormalizerConfig = [:]
    Map schemaUpdateConfig = [:]
    int maxFiles
    Sql sql
    Map urlsToSearch = EzproxyWorkflowDefaults.URLS_TO_SEARCH
    String masterTable = "ez_master"
    def pipeline = ["updateSchema", "loadAndNormalizeTarget"]

    Sql getSql() {
        if (sql) {
            return sql
        }

        sql = new Sql(dataSource)
    }

    boolean shouldRun() {
        def workingDirectory = new File(getLoadFileIntoTable().workingDirectory)
        def result = false
        if (workingDirectory.exists()) {
            workingDirectory.eachFile {
                if (it.isFile()) {
                    result = true
                }
            }
        }

        return result
    }

    void disableKeysOnMaster() {
        if (shouldRun()) {
            getSql().execute("alter table ${masterTable} disable keys" as String)
        }
    }

    void enableKeysOnMaster() {
        if (shouldRun()) {
            getSql().execute("alter table ${masterTable} enable keys" as String)
        }
    }

    synchronized SqlNormalizer getSqlNormalizer() {
        if (sqlNormalizer) {
            return sqlNormalizer
        }

        if (!sqlNormalizerConfig.containsKey("dataSource")) {
            sqlNormalizerConfig.dataSource = dataSource
        }

        if (!sqlNormalizerConfig.containsKey("sourceTable")) {
            sqlNormalizerConfig.sourceTable = getLoadFileIntoTable().loadingTable
        }

        if (!sqlNormalizerConfig.containsKey("destinations")) {
            sqlNormalizerConfig.destinations = EzproxyWorkflowDefaults.DEFAULT_NORMALIZATION_DESTINATIONS
        }

        sqlNormalizer = new SqlNormalizer(sqlNormalizerConfig)
    }

    synchronized LoadFileIntoTable getLoadFileIntoTable() {
        if (loadFileIntoTable) {
            return loadFileIntoTable
        }

        if (!loadFileToTableConfig.containsKey("dataSource")) {
            loadFileToTableConfig.dataSource = dataSource
        }

        if (!loadFileToTableConfig.containsKey("validatorMap")) {
            loadFileToTableConfig.validatorMap = EzproxyWorkflowDefaults.DEFAULT_VALIDATION_MAP
        }

        loadFileIntoTable = new LoadFileIntoTable(loadFileToTableConfig)
    }

    void loadFromFile() {
        getLoadFileIntoTable().run()
    }

    void loadAndNormalize() {

        if (shouldRun()) {
            def workingDirectory = getLoadFileIntoTable().workingDirectory
            def directory = new File(workingDirectory)
            (1..maxFiles).each {
                def continueProcessing = {
                    def result = false
                    directory.listFiles().each {
                        if (it.isFile()) {
                            result = true
                        }
                    }

                    return result
                }
                if (continueProcessing()) {
                    log.info("loading from file")
                    loadFromFile()
                    log.info("normalizing data")
                    normalize()
                    log.info("normalizing url references")
                    normalizeUrlReferences()
                    log.info("truncating loading file")
                    String truncate = "truncate ${loadFileIntoTable.loadingTable}"
                    getSql().execute(truncate)
                    log.info("finished loading 1 file")
                }
            }
        }

        log.info("finished loading all files")
    }

    void normalize() {
        getSqlNormalizer().run()
    }

    void normalizeUrlReferences() {
        def newLine = SystemUtils.LINE_SEPARATOR
        def sqlTemplate = {String table, boolean isRef, String regex ->

            def urlText = "url"
            if (isRef) {
                urlText = "ref_${urlText}"
            }

            "insert ignore into ${table}(${table}.url_key)${newLine}" +
                "                        select ${urlText}_key${newLine}" +
                "                        from ezproxy_loading${newLine}" +
                "                        where ${urlText} regexp '${regex}'"
        }


        urlsToSearch.each {String table, String regex ->
            String sqlText = sqlTemplate(table, true, regex)
            log.info("resolving ${table} with sql: ${newLine}${sqlText}")
            getSql().execute(sqlText)
            sqlText = sqlTemplate(table, false, regex)
            log.info("resolving ${table} with sql: ${newLine}${sqlText}")
            getSql().execute(sqlText)
        }
    }


    def loadGantScriptsAndTargets() {
        JobBuilder.isJob(this)

        binding.dataSource = dataSource
        includeTargets << _UpdateSchema
        includeTargets << _TableKeyOperations

        liquibaseDataSource = dataSource
        liquibaseFile = "schemas/ezproxy/ezproxySchema.xml"

        target(disableKeysOnMasterTarget: "disables keys on master") {
            disableKeysOnMaster()
        }

        target(loadAndNormalizeTarget: "loads and normalizes data") {
            loadAndNormalize()
        }

        target(enableKeysOnMasterTarget: "enables keys on master") {
            enableKeysOnMaster()
        }

        target(runEzproxy: "runs the ezproxy pipeline") {
            depends(pipeline)
        }
    }

    @Override
    Object run() {
        loadGantScriptsAndTargets()

        executeTargets(["runEzproxy"])
    }
}

class EzproxyWorkflowDefaults {
    static final Map URLS_TO_SEARCH = [
        ez_sfx_resources: "/elinks.",
        ez_doi_resources: "/doi/",
        ez_pmid_resources: "pmid:[0-9]{8}",
    ]

    private static final Map DEFAULT_VALIDATION_MAP_ITEM = [type: String, length: 32]
    private static final Map DEFAULT_KEY_TYPE = [type: byte[]]

    static final Map DEFAULT_VALIDATION_MAP = [
        ref_url_key: DEFAULT_KEY_TYPE,
        url_key: DEFAULT_KEY_TYPE,
        source_file_key: DEFAULT_KEY_TYPE,
        ezproxy_id_key: DEFAULT_KEY_TYPE,
        response_key: DEFAULT_KEY_TYPE,
        agent_key: DEFAULT_KEY_TYPE,
        patron_address_key: DEFAULT_KEY_TYPE,
        patron_id_key: DEFAULT_KEY_TYPE,
        patron_ip_key: DEFAULT_KEY_TYPE,
        patron_ip: [
            regex: /\b\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}\b/,
            type: String
        ],
        city: DEFAULT_VALIDATION_MAP_ITEM,
        http_method: [
            length: 12,
            type: String
        ],
        state: [
            length: 2,
            type: String
        ],
        agent: [
            length: 1024,
            type: String
        ],
        source_file: DEFAULT_VALIDATION_MAP_ITEM,
        line_num: [
            type: Integer,
        ],
        country: DEFAULT_VALIDATION_MAP_ITEM,
        patron_id: DEFAULT_VALIDATION_MAP_ITEM,
        http_status: [
            type: Integer,
        ],
        response_size: [
            type: Integer,
        ],
        ref_url: [
            type: String,
            length: 2000
        ],
        url: [
            type: String,
            length: 2000
        ],
        ezproxy_id: DEFAULT_VALIDATION_MAP_ITEM,
    ]

    static final Map DEFAULT_NORMALIZATION_DESTINATIONS = [

        ez_patron_ip: [
            columns: ["patron_ip", "patron_ip_key"],
            key: "patron_ip_key"
        ],
        ez_patron_address: [
            columns: ["city", "state", "country", "patron_address_key"],
            key: "patron_address_key"
        ],
        ez_response: [
            columns: ["http_method", "http_status", "response_size", "response_key"],
            key: "response_key"
        ],
        ez_agent: [
            columns: ["agent", "agent_key"],
            key: "agent_key"
        ],
        ez_cookies: [
            columns: ["cookies", "cookies_key"],
            key: "cookies_key"
        ],
        ez_patron_id: [
            columns: ["patron_id", "patron_id_key"],
            key: "patron_id_key"
        ],
        ez_source_file: [
            columns: ["source_file", "source_file_key"],
            key: "source_file_key"
        ],

        ez_ezproxy_id: [
            columns: ["ezproxy_id", "ezproxy_id_key"],
            key: "ezproxy_id_key"
        ],

        ez_resources: [
            columns: ["url", "url_key"],
            key: "url_key"
        ],

        ez_ref_resources: [
            columns: ["ref_url", "ref_url_key"],
            key: "url_key",
            table: "ez_resources",
            columnMap: [
                ref_url: "url",
                ref_url_key: "url_key"
            ]
        ],

        ez_master: [
            columns: ["ezproxy_id_key", "proxy_time", "patron_ip_key", "patron_address_key", "patron_id_key", "response_key", "agent_key", "line_num", "load_time", "url_key", "source_file_key", "cookies_key", "ref_url_key"]
        ],

        ez_lines_by_file_name: [
            sql: """
                insert into ez_lines_by_file_name(source_file, lines_processed)
                select a.source_file, count(a.source_file) as lines_processed from
                  (select a.source_file_key, a.source_file
                    from ez_source_file a
                    left join ez_lines_by_file_name b on
                      a.source_file = b.source_file where b.source_file is null) a, ez_master b

                where a.source_file_key = b.source_file_key
                group by source_file
            """
        ]
    ]

    private static final DEFAULT_SCHEMA_UPDATE_CONFIG = [
        schema: "schemas/ezproxy/ezproxySchema.xml"
    ]
}