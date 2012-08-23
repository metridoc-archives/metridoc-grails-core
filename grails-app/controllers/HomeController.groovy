class HomeController {

    def grailsApplication
    def homeService

    def index() {

        def adminLinks = homeService.getAdminApps(this)
        def reportLinks = homeService.getAvailableApplications(this)
        return [
            reportLinks: reportLinks,
            adminLinks: adminLinks,
            hasLinks: reportLinks.size() + adminLinks.size(),
        ]

    }

}
