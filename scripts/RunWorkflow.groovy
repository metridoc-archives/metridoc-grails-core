import org.apache.commons.lang.SystemUtils
import org.codehaus.groovy.runtime.InvokerInvocationException
import org.codehaus.groovy.grails.commons.GrailsClass

includeTargets << grailsScript('_GrailsBootstrap')

newLine = SystemUtils.LINE_SEPARATOR

target(main: "Runs a workflow via the commandline instead of in he application") {
    depends parseArguments, checkAndRunServer
    def s = new Socket(InetAddress.getLocalHost(), 4444);
    try {
        s.withStreams { input, output ->
            if (argsMap.x) {
                output << "exit${newLine}"
            } else {
                output << argsMap.params[0] + "${newLine}"
            }
            def response = input.newReader().readLine()
            event('StatusUpdate', ["client received message [${response}] from server"])
        }
    } finally {
        s.close()
    }
}

setDefaultTarget(main)

target(checkAndRunServer: "checks if a server is already running, if not starts one up") {
    try {
        workflowServer = new ServerSocket(4444)
        depends handleServer
    } catch (BindException e) {
        event('StatusUpdate', ["Workflow server is running"])
    }

    def pingException
    def thread = Thread.start {
        def s = new Socket(InetAddress.getLocalHost(), 4444);
        def buffer
        try {
            s.withStreams { input, output ->
                output << "ping${newLine}"
                buffer = input.newReader().readLine()
                event('StatusUpdate', ["Workflow server is receiving messages"])
            }
        } catch (Throwable ex) {
            pingException = ex
        } finally {
            s.close()
        }
    }

    try {
        thread.join(2000)
        if (thread.isAlive()) {
            throw new RuntimeException("Not receiving anything from the workflow server")
        }
    } catch (Throwable throwable) {
        pingException = throwable
    }

    if (pingException) {
        grailsConsole.error("Workflow server is not running properly, must exit shell", pingException)
        System.exit(1)
    }
}

target(handleServer: "boots and runs the server") {

    event('StatusUpdate', ["Booting up workflow server..."])

    Thread.start {
        def notDone = true
        while (notDone) {
            try {
                def socket = workflowServer.accept()
                socket.withStreams { input, output ->
                    def reader = input.newReader()
                    def buffer = reader.readLine()
                    if (buffer.startsWith("exit")) {
                        notDone = false
                        event('StatusUpdate', ["Workflow server is shutting down..."])
                        workflowServer.close()
                        output << "serverDown${newLine}"
                        event('StatusUpdate', ["Workflow server is shut down"])
                    } else if (buffer.startsWith("ping")) {
                        output << "receiving${newLine}"
                    } else {
                        try {
                            compile()
                            loadAllWorkflows()
                            event('StatusUpdate', ["Server running command ${buffer}"])
                            if ("start" != buffer) {
                                binding."${buffer}".call()
                            }
                        } catch (Throwable ex) {
                            grailsConsole.error("Could not run ${buffer}: ${ex.message}", ex);
                        } finally {
                            output << "done${newLine}"
                        }
                    }
                }

            } catch (InvokerInvocationException ex) {
                def throwIt = true
                def cause = ex.cause
                if (cause instanceof SocketException) {
                    def socketClosed = cause.message.contains("Socket closed")
                    if (socketClosed) {
                        throwIt = false
                        grails.warn "Socket closed while trying to accept messages from workflow server"
                    }
                }
                if (throwIt) {
                    throw ex
                }
            }
        }

    }
}

target(loadAllWorkflows: "loads all workflows") {
    depends bootstrapOnce
    event('StatusUpdate', ["Loading all workflows"])
    grailsApp.workflowClasses.each {GrailsClass grailsClass ->
        if (!grailsClass.isAbstract()) {
            event('StatusUpdate', ["Loading workflow ${grailsClass.getName()}"])
            def reference = grailsClass.getReferenceInstance()
            def wrapper = reference.wrapper
            def name = grailsClass.getName()
            reference.binding = binding
            try {
                wrapper.run()
            } catch (Exception ex) {
                grailsConsole.error "Could not load workflow ${name}: ${ex.message}", ex
            }
        }
    }
    event('StatusUpdate', ["Workflows loaded"])
}
