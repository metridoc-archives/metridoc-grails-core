package metridoc.core

class HomeController {

    def homeService

    def index() {
        def model = [:]

        model.categories = homeService.controllersByCategory

        return model
    }
}
