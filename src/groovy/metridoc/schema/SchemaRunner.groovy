package metridoc.schema

import javax.annotation.PostConstruct
import metridoc.liquibase.MetridocLiquibase

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 7/17/12
 * Time: 4:59 PM
 */
class SchemaRunner {

    def dataSource
    def schema

    @PostConstruct
    def init() {
        new MetridocLiquibase(dataSource: dataSource, schema: schema).runMigration()
    }
}
