package metridoc.core

import grails.util.Environment
import org.codehaus.groovy.runtime.InvokerInvocationException
import org.apache.commons.lang.StringUtils
import org.apache.commons.lang.SystemUtils

class DevelopmentWorkflowRunnerService {

    def quartzService
    def grailsApplication
    def workflowServer

    /**
     * called via Bootstrap
     */
    def startWorkflowServer() {
        if (canStartWorkflowServer(Environment.current)) {
            Thread.start {
                try {
                    def notDone = true
                    workflowServer = new ServerSocket(4444)
                    def newLine = SystemUtils.LINE_SEPARATOR
                    log.info "workflow server running and waiting for messages"
                    while (notDone) {
                        try {
                            def socket = workflowServer.accept()
                            socket.withStreams { input, output ->
                                def message = "done"
                                def reader = input.newReader()
                                def buffer = reader.readLine()

                                if (buffer.startsWith("ping")) {
                                    output << "receiving${newLine}"
                                } else {
                                    try {
                                        runWorkflow(buffer)
                                    } catch (Throwable ex) {
                                        log.error("Could not run ${buffer}: ${ex.message}", ex);
                                        message = "error"
                                    } finally {
                                        output << "${message}${newLine}"
                                    }
                                }
                            }

                        } catch (SocketException ex) {
                            if(ex.message.contains("Socket closed"))
                            log.debug("Socket was closed, workflow server has likely been shutdown")
                            notDone = false
                        }
                    }
                } catch (Exception e) {
                    log.error("unexpected error with workflow server, shutting it down", e)
                    try {
                        workflowServer.close()
                    } catch (Exception exception) {
                        //do nothing, fail silently
                    }
                }
            }
        }
    }

    private canStartWorkflowServer(Environment environment) {
        switch (environment) {
            case Environment.DEVELOPMENT:
            case Environment.TEST:
                //start server
                return true
                break
            default:
                log.info "cannot start workflow server in environment ${environment}, " +
                        "it can only run during development or testing"
                return false
        }
    }

    def runWorkflow(String workflowName) {
        def unCappedWorkflowName = StringUtils.uncapitalise(workflowName)
        def workflow = quartzService.workflowsByName[unCappedWorkflowName]
        assert workflow: "workflow $workflowName does not exist"
        workflow.run()
    }

    def stopWorkflowServer() {
        if(workflowServer) {
            log.info "closing workflow server"
            workflowServer.close()
        }
    }
}
