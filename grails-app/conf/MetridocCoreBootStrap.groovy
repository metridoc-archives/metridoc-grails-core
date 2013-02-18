
class MetridocCoreBootStrap {

    def initAuthService
    def quartzService

    def init = { servletContext ->
        initAuthService.init()
        quartzService.initializeJobs()
    }

    def destroy = {
    }
}