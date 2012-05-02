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
package metridoc.workflows.validation.impl

import groovy.sql.Sql
import javax.sql.DataSource
import metridoc.plugins.schema.MetridocLiquibase
import metridoc.workflows.validation.ValidationErrorHandler
import org.apache.camel.Exchange
import org.apache.commons.lang.exception.ExceptionUtils

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 2/13/12
 * Time: 4:12 PM
 */
class FileIngestorValidationErrorHandler implements ValidationErrorHandler<Exchange> {

    public static final String DEFAULT_SCHEMA = "schemas/validation/validationSchema.xml"
    public static final String DEFAULT_VALIDATION_TABLE = "validation_errors"
    public static final String DATASOURCE_CANNOT_BE_NULL = "datasource cannot be null"
    public static final String SCHEMAS_CANNOT_BE_NULL_OR_EMPTY = "schemas cannot be empty or null"

    DataSource dataSource
    Sql _sql
    private boolean checkedSchema = false
    String validationErrorScope = "UNKNOWN"
    List<String> validationErrorSchemas = [DEFAULT_SCHEMA]
    String validationTable = DEFAULT_VALIDATION_TABLE
    boolean runLiquibase = true

    Sql getSql() {
        (Sql) checkDataSourceAndRun {
            if (_sql) {
                return _sql
            }

            _sql = new Sql(dataSource)
        }
    }

    void handle(AssertionError error, Exchange record) {
        def insert = "insert into $validationTable (file_name, record_index, scope, record, error_message, stack_trace) values (?1.file_name, ?1.record_index, ?1.scope, ?1.record, ?1.error_message, ?1.stack_trace)"
        def params = [:]

        params.file_name = record.in.getHeader(Exchange.FILE_NAME_ONLY)
        params.record_index = record.getProperty(Exchange.SPLIT_INDEX)

        params.scope = validationErrorScope
        params.error_message = error.message
        params.stack_trace = ExceptionUtils.getStackTrace(error)
        params.record = record.in.body.toString()

        getSql().execute(insert, params)
    }

    private checkDataSourceAndRun(Closure closure) {
        assert dataSource: DATASOURCE_CANNOT_BE_NULL
        return closure.call()
    }

    public void updateSchema() {

        if (runLiquibase) {
            checkDataSourceAndRun {
                assert validationErrorSchemas: SCHEMAS_CANNOT_BE_NULL_OR_EMPTY

                if (!checkedSchema) {
                    validationErrorSchemas.each {
                        new MetridocLiquibase(schema: it, dataSource: dataSource).runMigration()
                    }
                    checkedSchema = true
                }
            }
        }
    }
}
