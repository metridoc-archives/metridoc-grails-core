
class MetridocCoreBootStrap {

    def initAuthService
    def quartzService

    def init = { servletContext ->
        initAuthService.init()
        quartzService.checkForGroovyDistribution()
        quartzService.initializeJobs()
    }

    def destroy = {
    }
}