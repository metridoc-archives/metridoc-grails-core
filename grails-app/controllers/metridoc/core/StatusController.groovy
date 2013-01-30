package metridoc.core

import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy

class StatusController {

    TransactionAwareDataSourceProxy dataSource

    static accessControl = {
        role(name:  "ROLE_ADMIN")
    }

    def index() {
        [
                dataSourceUrl: dataSource.connection.metaData.getURL()
        ]

    }
}
