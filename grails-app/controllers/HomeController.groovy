class HomeController {

    def homeService

    def index() {
        def model = [:]
        model << homeService.layoutConfig
        model.controllers = homeService.controllerInformation
        return model
    }

}
