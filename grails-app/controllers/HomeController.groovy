class HomeController {

    def grailsApplication
    def homeService

    def index() {
//        def adminLinks = []
//        def reportLinks = []
//        def layout = grailsApplication.config.metridoc.style.home.layout
//
//        grailsApplication.controllerClasses.each {
//            def clazz = it.clazz
//            def artifactName = StringUtils.uncapitalize(it.name)
//            def config = ReportsConfiguration.find() {
//                name == artifactName
//            }
//
//            def role = config ? config.role : null
//
//            def hasRole = SecurityUtils.subject.hasRole(role) || SecurityUtils.subject.hasRole("ROLE_ADMIN")
//
//            if(hasRole) {
//
//                def reportName = ClassUtils.getStaticVariable(clazz, "reportName")
//                def isAdmin = "ROLE_ADMIN" == role
//
//                if (isAdmin && reportName) {
//                    adminLinks << [
//                        reportName: reportName,
//                        controllerName: it.name
//                    ]
//                } else if (reportName) {
//                    reportLinks << [
//                        reportName: reportName,
//                        controllerName: it.name
//                    ]
//                }
//            }
//        }

        def adminLinks = homeService.getAdminApps(this)
        def reportLinks = homeService.getAvailableApplications(this)
        return [
            reportLinks: reportLinks,
            adminLinks: adminLinks,
            hasLinks: reportLinks.size() + adminLinks.size(),
        ]
    }

}
