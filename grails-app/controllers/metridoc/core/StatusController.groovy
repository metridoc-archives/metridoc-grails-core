package metridoc.core

class StatusController {

    def dataSource
    def grailsApplication

    static accessControl = {
        role(name: "ROLE_ADMIN")
    }

    def index() {
        [
                dataSourceUrl: dataSource.connection.metaData.getURL(),
                applicationName: grailsApplication.mergedConfig.metridoc.app.name,
                shiroFilters: grailsApplication.config.security.shiro.filter.filterChainDefinitions
        ]

    }
}
