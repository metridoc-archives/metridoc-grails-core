
class MetridocCoreBootStrap {

    def initAuthService

    def init = { servletContext ->
        println "bootstrapping metridoc"
        initAuthService.init()
    }

    def destroy = {
    }
}