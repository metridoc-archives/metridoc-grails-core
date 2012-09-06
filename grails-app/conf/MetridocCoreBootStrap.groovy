
class MetridocBootStrap {

    def initAuthService
    def quartzService

    def init = { servletContext ->
        initAuthService.init()
        quartzService.scheduleJobs()
    }
}