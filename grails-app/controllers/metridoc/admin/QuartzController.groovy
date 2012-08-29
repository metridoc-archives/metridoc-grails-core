package metridoc.admin

import org.apache.commons.lang.StringUtils
import org.apache.commons.lang.exception.ExceptionUtils

class QuartzController {

    def quartzService

    def index() {
        chain(action: "list")
    }

    def exception() {
        def workflowName = params.id
        def workflow = null
        if (workflowName) {
            workflow = quartzService.workflowsByName[workflowName]
        }

        if (!workflow) {
            render(status: 400, view: "exception", model: [workflowException: "Workflow not specified"])
        } else {
            def errorMessage = null
            def exception = workflow.lastException

            if (exception) {
                errorMessage = ExceptionUtils.getStackTrace(exception).encodeAsHTML()
            }

            return [
                    capitalizedWorkflowName: StringUtils.capitalise(workflowName),
                    errorMessage: errorMessage,
                    noErrorMessage: "$workflowName does not have an error",
            ]
        }
    }

    def list() {
        def workflows = quartzService.listWorkflows(params)

        def workflowCount = quartzService.totalWorkflowCount()
        def showPagination = workflowCount > quartzService.getMax(params)

        return [
                workflows: workflows,
                showPagination: showPagination,
                workflowCount: workflowCount
        ]
    }
}
