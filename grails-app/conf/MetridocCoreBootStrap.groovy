
class MetridocBootStrap {

    def initAuthService
    def quartzService
    def schemaService

    def init = { servletContext ->
        println "bootstrapping metridoc"
        schemaService.migrateDbSchemas()
        initAuthService.init()
        quartzService.scheduleJobs()
    }
}