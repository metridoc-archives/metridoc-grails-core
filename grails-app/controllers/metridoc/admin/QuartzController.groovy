package metridoc.admin

class QuartzController {

    def quartzService

    def index() {
        chain(action: "list")
    }

    def list() {
        def workflows = quartzService.listWorkflows(params)

        def workflowCount = quartzService.totalWorkflowCount()
        def showPagination = workflowCount > QuartzService.getMax(params)

        return [
            workflows: workflows,
            showPagination: showPagination
        ]
    }
}
