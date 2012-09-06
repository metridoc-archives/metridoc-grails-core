
class MetridocBootStrap {

    def initAuthService
    def quartzService
    def ingestorInitializerService

    def init = { servletContext ->
        initAuthService.init()
        quartzService.scheduleJobs()
        ingestorInitializerService.init()
    }
}