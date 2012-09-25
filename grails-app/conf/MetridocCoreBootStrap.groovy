
class MetridocCoreBootStrap {

    def initAuthService
    def quartzService
    def schemaService
    def developmentWorkflowRunnerService

    def init = { servletContext ->
        println "bootstrapping metridoc"
        schemaService.migrateDbSchemas()
        initAuthService.init()
        quartzService.scheduleJobs()
        developmentWorkflowRunnerService.startWorkflowServer()
    }

    def destroy = {
        developmentWorkflowRunnerService.stopWorkflowServer()
    }
}