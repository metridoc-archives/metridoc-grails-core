package metridoc.admin

import org.apache.commons.lang.StringUtils
import org.apache.commons.lang.exception.ExceptionUtils

class QuartzController {

    def quartzService

    def index() {
        chain(action: "list", params: params)
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

    def jobListOnly() {
        def workflows = quartzService.listWorkflows(params)
        def workflowCount = quartzService.totalWorkflowCount()
        def showPagination = workflowCount > quartzService.getMax(params)
        return [
            workflows: workflows,
            showPagination: showPagination,
            workflowCount: workflowCount
        ]
    }

    def runJob() {
        def workflowName = params.id
        if (workflowName) {
            quartzService.runJob(workflowName)
            render "job ${workflowName} running"
        } else {
            render "no job specified"
        }
    }

    def show() {

        def run = params.run
        if (run) {
            def workflowToShow = quartzService.workflowsByName[run]

            if (workflowToShow) {
                log.info "RUN WORKFLOW ${workflowToShow}"
                Thread.start {
                    workflowToShow.run()
                }
            } else {
                log.info "Could not run $run since it does not exist"
            }
            params.remove("run")
            params.put("id", run)

            chain(action: "show", params: params)
            return
        }

        def unCapName = params.id
        def workflowToShow = quartzService.getAWorkflow(unCapName)
        def workflowErrorMsg = quartzService.getWorkflowErrorMsg(unCapName)

        return [
            capitalizedWorkflowName: StringUtils.capitalise(unCapName),
            workflowToShow: workflowToShow,
            errorMessage: workflowErrorMsg,
        ]
    }

    def showException() {
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


}
