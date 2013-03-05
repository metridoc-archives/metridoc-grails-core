
class MetridocCoreBootStrap {

    def initAuthService
    def initQuartzService

    def init = { servletContext ->
        initAuthService.init()
        initQuartzService.initializeScheduler()
    }

    def destroy = {
    }
}