package metridoc.admin

import org.apache.commons.lang.StringUtils
import org.apache.commons.lang.exception.ExceptionUtils

class QuartzController {

    def quartzService

    def index() {
        chain(action: "list", params: params)
    }

    def list() {

        def run = params.run

        if (run) {
            def workflowToRun = quartzService.workflowsByName[run]
            if (workflowToRun) {
                Thread.start {
                    workflowToRun.run()
                }
            } else {
                log.info "Could not run $run since it does not exist"
            }
            params.remove("run")
            chain(action: "list", params: params)
        } else {
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

    def show(){
        log.info "show workflow from here"
        def run = params.run
        if (run) {
            def workflowToRun = quartzService.workflowsByName[run]
            if (workflowToRun) {
                Thread.start {
                    workflowToRun.run()
                }
            } else {
                log.info "Could not run $run since it does not exist"
            }
            params.remove("run")
            chain(action: "show", params: params)
        }else{
            def name = params.id
            log.info "Workflow to show name ${name}"
            def workflowToShow = quartzService.getAWorkflow(params)
            log.info "Will be showing workflow ${workflowToShow}NAME${workflowToShow.name}#${workflowToShow.previousFireTime}#${workflowToShow.nextFireTime}#${workflowToShow.previousDuration}"
            return [
                    workflowToShow:workflowToShow
            ]
        }

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
}
