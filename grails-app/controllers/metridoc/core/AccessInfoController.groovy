package metridoc.core

class AccessInfoController {

    def accessInfoService

    def index() {
        [
            links: accessInfoService.buildHomeLinks()
        ]
    }
}
