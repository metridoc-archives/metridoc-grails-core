package metridoc.schema

import javax.annotation.PostConstruct
import metridoc.liquibase.MetridocLiquibase

/**
 *
 * @deprecated use spring liquibase capabilities or the grails migration plugin instead
 */
class SchemaRunner {

    def dataSource
    def schema

    @PostConstruct
    def init() {
        new MetridocLiquibase(dataSource: dataSource, schema: schema).runMigration()
    }
}
