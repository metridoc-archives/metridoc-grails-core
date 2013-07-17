class MetridocCoreBootStrap {

    def initAuthService
    def homeService

    def init = { servletContext ->
        initAuthService.init()
        homeService.bootStrapApplications()
    }

    def destroy = {
    }
}