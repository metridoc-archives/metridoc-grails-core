package metridoc.admin

class ManageReportsService {

    def updateRoles(Map params) {
        params.each {
            def m = it.key =~ /^role_(.+)$/
            if (m.matches()) {
                def configuration = ReportsConfiguration.find {
                    name == m.group(1)
                }
                configuration.role = it.value
                configuration.save()
            }

        }
    }
}
