includeTargets << grailsScript("_GrailsInit")

doCliCall = {
    System.setProperty("metridoc.quartz.disabled", "true")
    System.setProperty("metridoc.job.cliOnly", "true")
    grailsConsole.info "running the job ${runJobArguments.job} directly"

    def job = appCtx."${runJobArguments.job}"
    if (job) {
        def MetridocJob = classLoader.loadClass("metridoc.core.MetridocJob")
        if (MetridocJob.isAssignableFrom(job.class)) {
            grailsConsole.info "running as MetridocJob"
            if (runJobArguments.target) {
                job.executeTarget(runJobArguments.target)
            } else {
                job.executeTarget()
            }
        } else {
            job.execute()
        }
    } else {
        grailsConsole.error "The job ${runJobArguments.job} does not exist"
        exit(-1)
    }
}

doRestCall = {
    def baseUrl = "${runJobArguments.protocol}://${runJobArguments.host}/${grailsAppName}/rest/quartz/"
    def url = "${baseUrl}runNow?jobGroup=DEFAULT&jobName=${argsMap.params[0]}${runJobArguments.targetQuery}&returnTriggerId"

    if (runJobArguments.testRun) {
        grailsConsole.info "run-job url is ${url} with username ${runJobArguments.user} and password ${runJobArguments.password}"
    } else {
        def restBuilder = classLoader.loadClass("grails.plugins.rest.client.RestBuilder").newInstance()
        try {
            def response = restBuilder.get(url) {
                auth(runJobArguments.user, runJobArguments.password)
            }

            grailsConsole.info "response returned with a response status of $response.status"

            def triggerId = response.text
            grailsConsole.info "triger id is: $triggerId"
            def status = "running"
            while (status == "running") {
                def statusUrl = "${baseUrl}status/${triggerId}"
                status = restBuilder.get(statusUrl) {
                    auth(runJobArguments.user, runJobArguments.password)
                }.text
                Thread.sleep(500)
            }

            if (status != "complete") {
                grailsConsole.error "Job ended with status ${status}, some error occurred, if it wasn't logged to console, look in file logs"
                exit(-1)
            }

            return true
        } catch (Exception ex) {
            if (ex.message.contains("Connection refused")) {
                grailsConsole.warn "Can't run a job via rest, the application is probably not running.  Going to run a straight command line call"
                return false
            }
        }
    }
}