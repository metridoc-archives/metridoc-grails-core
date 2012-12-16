class HomeController {

    def homeService

    static final homePage = [
            exclude: true
    ]

    def index() {
        def model = [:]
        model.adminControllers = homeService.adminControllers
        model.applicationControllers = homeService.applicationControllers

        return model
    }

}
