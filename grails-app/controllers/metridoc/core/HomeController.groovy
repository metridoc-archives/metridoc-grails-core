package metridoc.core

class HomeController {

    def homeService

    def index() {
        [categories: homeService.categories]
    }
}
