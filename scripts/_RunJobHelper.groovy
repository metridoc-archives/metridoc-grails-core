import groovy.json.JsonSlurper

includeTargets << grailsScript("_GrailsInit")

doCliCall = {

    def job = appCtx."${runJobArguments.job}"
    if (job) {
        def MetridocJob = classLoader.loadClass("metridoc.core.MetridocJob")
        if (MetridocJob.isAssignableFrom(job.class)) {
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

            def triggerId = response.text
            def status = "running"
            while (status == "running") {
                def statusUrl = "${baseUrl}status/${triggerId}"
                def statusResponse = restBuilder.get(statusUrl) {
                    auth(runJobArguments.user, runJobArguments.password)
                }
                assert statusResponse.status == 200 : "error occurred checking job status:\n$statusResponse.text"
                status = statusResponse.json.status

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