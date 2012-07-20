
class MetridocBootStrap {

    def initAuthService
    def initReportsService

    def init = { servletContext ->

        initAuthService.init()
        initReportsService.init()

    }


}