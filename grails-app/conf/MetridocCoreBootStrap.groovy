
class MetridocCoreBootStrap {

    def initAuthService
    def quartzService

    def init = { servletContext ->
        initAuthService.init()
//      not sure we need this anymore
//        quartzService.checkForGroovyDistribution()
        quartzService.initializeJobs()
    }

    def destroy = {
    }
}