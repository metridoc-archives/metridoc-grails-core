/*
 runs a job restfully.  By default it constructs a url to local host with user name admin and password password.
*/
includeTargets << grailsScript("_GrailsBootstrap")

runJobUsage = """
////////////////////////////////
// RUN-JOB USAGE              //
////////////////////////////////

grails run-job [options] <job name>

Options:

         h: displays this message
  protocol: http or https, http by default
      host: supplies the host for the job call, by default this is localhost:8080
  test-run: prints the url used to run the job without actually calling it
       cli: bootstrap application and call job directly
    target: the target to call in job dsl
      user: user name to use (default 'admin')
  password: password to use (default 'password')

"""

runJobArguments = [:]

target(main: "The description of the script goes here!") {
    depends(parseArguments)
    parseArgumentsAndSetDefaults()
    if (argsMap.h) {
        usage()
    } else if (argsMap.params.size != 1) {
        grailsConsole.error "1 job must be specified, run grails run-job -h for more info"
    } else if (argsMap.cli) {
        System.setProperty("metridoc.quartz.disabled", "true")
        System.setProperty("metridoc.job.cliOnly", "true")
        grailsConsole.info "running the job ${runJobArguments.job} directly"
        depends(packageApp, loadApp, configureApp)
        def job = appCtx."${runJobArguments.job}"
        def MetridocJob = classLoader.loadClass("metridoc.core.MetridocJob")
        if (job) {
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

    } else {

        def baseUrl = "${runJobArguments.protocol}://${runJobArguments.host}/${grailsAppName}/rest/quartz/"
        def url = "${baseUrl}runNow?jobGroup=DEFAULT&jobName=${argsMap.params[0]}${runJobArguments.targetQuery}&returnTriggerId"

        if (runJobArguments.testRun) {
            grailsConsole.info "run-job url is ${url} with username ${runJobArguments.user} and password ${runJobArguments.password}"
        } else {
            depends(packageApp)
            def restBuilder = classLoader.loadClass("grails.plugins.rest.client.RestBuilder").newInstance()
            def triggerId = restBuilder.get(url) {
                auth(runJobArguments.user, runJobArguments.password)
            }.text
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
        }
    }

}

def usage() {
    grailsConsole.info runJobUsage
}

def parseArgumentsAndSetDefaults() {
    runJobArguments.host = argsMap.host ?: "localhost:8080"
    runJobArguments.testRun = argsMap."test-run" ? true : false
    runJobArguments.protocol = argsMap.protocol ?: "http"
    runJobArguments.target = argsMap.target
    runJobArguments.targetQuery = argsMap.target ? "&target=${argsMap.target}" : ""
    runJobArguments.user = argsMap.user ?: "admin"
    runJobArguments.password = argsMap.password ?: "password"
    runJobArguments.job = argsMap.params ? argsMap.params[0] : null
}

setDefaultTarget(main)
