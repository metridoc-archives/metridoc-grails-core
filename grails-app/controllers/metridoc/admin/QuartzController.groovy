package metridoc.admin

class QuartzController {

    def index() {
        chain(action: "list")
    }

    def list() {
        def workflows = []

        def max = Math.min(params.max ? params.int('max') : 10, 100)
        params.max = max
        def workflowClasses = grailsApplication.workflowClasses
        def workflowCount = workflowClasses.size()
        def showPagination = workflowCount > max

        workflowClasses.each {
            workflows << [name:"$it.name"]
        }

        return [
            workflows: workflows,
            showPagination: showPagination
        ]
    }
}
