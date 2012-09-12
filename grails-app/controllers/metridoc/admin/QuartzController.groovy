package metridoc.admin

import org.apache.commons.lang.StringUtils

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
            log.info "NAME ${name}"
            def workflowToShow = quartzService.getAWorkflow(params)
            def workflowErrorMsg = quartzService.getWorkflowErrorMsg(name)
            log.info "Will be showing workflow ${workflowToShow}NAME${workflowToShow.name}#${workflowToShow.previousFireTime}#${workflowToShow.nextFireTime}#${workflowToShow.previousDuration}"
            log.info "ERROR ${workflowErrorMsg}"
            return [
                    capitalizedWorkflowName: StringUtils.capitalise(name),
                    workflowToShow:workflowToShow,
                    errorMessage: workflowErrorMsg,
            ]
        }

    }
//
//    def exception() {
//        def workflowName = params.id
//        def workflow = null
//        if (workflowName) {
//            workflow = quartzService.workflowsByName[workflowName]
//        }
//
//        if (!workflow) {
//            render(status: 400, view: "_exception", model: [workflowException: "Workflow not specified"])
//        } else {
//
//            def errorMessage = quartzService.getWorkflowErrorMsg(workflowName)
//            return [
//                    capitalizedWorkflowName: StringUtils.capitalise(workflowName),
//                    errorMessage: errorMessage,
//                    noErrorMessage: "$workflowName does not have an error",
//            ]
//        }
//
//    }
}
