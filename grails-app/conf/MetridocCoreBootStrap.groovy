
class MetridocCoreBootStrap {

    def initAuthService

    def init = { servletContext ->
        initAuthService.init()
    }

    def destroy = {
    }
}