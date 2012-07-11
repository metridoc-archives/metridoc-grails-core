package metridoc.targets

import metridoc.liquibase.MetridocLiquibase

runLiquibase = {Map params ->
    new MetridocLiquibase(params).runMigration()
}