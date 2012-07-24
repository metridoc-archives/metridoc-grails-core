

class HomeController {
    def homeService

    def index() {
        render(view: 'index', model: [applications:homeService.applications, reports: homeService.reports, administrativeApps:homeService.administrativeApps])
    }

}
