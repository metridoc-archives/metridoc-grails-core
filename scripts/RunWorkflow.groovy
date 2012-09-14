import org.apache.commons.lang.SystemUtils

includeTargets << grailsScript('_GrailsBootstrap')

newLine = SystemUtils.LINE_SEPARATOR

target(main: "Runs a workflow via the commandline instead of in he application") {
    depends parseArguments
    def serverRunning = checkServer()
    def workflowName = argsMap.params[0]
    if (serverRunning) {
        def s = new Socket(InetAddress.getLocalHost(), 4444);
        try {
            s.withStreams { input, output ->
                output << "${workflowName}${newLine}"
                def response = input.newReader().readLine()
                if("error" == response) {
                    throw new RuntimeException("error occurred running workflow ${workflowName}")
                }
                event('StatusUpdate', ["client received message [${response}] from server"])
            }
        } finally {
            s.close()
        }
    } else {
        grailsConsole.info "Server is not running, will have to bootstrap application.  Consider using run-app for faster workflow executions"
        depends bootstrap
        appCtx.developmentWorkflowRunnerService.runWorkflow(workflowName)
    }
}

checkServer = {
    def pingException
    def thread = Thread.start {
        def s
        try {
            s = new Socket(InetAddress.getLocalHost(), 4444);
            def buffer
            s.withStreams { input, output ->
                output << "ping${newLine}"
                buffer = input.newReader().readLine()
                event('StatusUpdate', ["Workflow server is receiving messages"])
            }
        } catch (Throwable ex) {
            pingException = ex
        } finally {
            try {
                s.close()
            } catch (Exception ex) {
                grailsConsole.error(ex.message)
            }
        }
    }

    thread.join(10000)

    if (pingException) {
        return false
    }

    if (thread.isAlive()) {
        return false
    }

    return true
}


setDefaultTarget(main)