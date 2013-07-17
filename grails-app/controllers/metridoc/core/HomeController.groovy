package metridoc.core

class HomeController {

    def homeService

    def index() {
        def model = [:]
        model.adminControllers = homeService.adminControllers
        model.applicationControllers = homeService.applicationControllers
        model.categories = homeService.categories

        return model
    }
}
