package metridoc.core

import metridoc.schema.SchemaRunner
import metridoc.liquibase.MetridocLiquibase
import javax.annotation.PostConstruct
import org.springframework.beans.factory.InitializingBean

class SchemaService {

    def grailsApplication

    def migrateDbSchemas() {
        def applicationContext = grailsApplication.mainContext
        grailsApplication.mergedConfig.metridoc.schemas.each {
            log.info "migrating dataSource ${it.key}"
            def dataSourceName = "dataSource_${it.value.dataSource}"
            def dataSource = applicationContext."${dataSourceName}"
            def schema = it.value.schema
            new MetridocLiquibase(schema: schema, dataSource:dataSource).runMigration()
        }
    }

}
