

class HomeController {
    def controllerHelperService

    def index() {
        render(view: 'index', model: [applications:controllerHelperService.applications, reports: controllerHelperService.reports, administrativeApps:controllerHelperService.administrativeApps])
    }

}
