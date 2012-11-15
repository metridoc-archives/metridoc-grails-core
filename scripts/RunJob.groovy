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
"""

runJobArguments = [:]

target(main: "The description of the script goes here!") {
    depends(parseArguments, packageApp)

    if (argsMap.h) {
        usage()
    } else if (argsMap.params.size != 1) {
        grailsConsole.error "1 job must be specified, run grails run-job -h for more info"
    } else {
        parseArgumentsAndSetDefaults()
        def restBuilder = classLoader.loadClass("grails.plugins.rest.client.RestBuilder").newInstance()
        def url = "${runJobArguments.protocol}://${runJobArguments.host}/${grailsAppName}/quartz/runNow?jobGroup=DEFAULT&jobName=${argsMap.params[0]}${runJobArguments.target}&returnTriggerId"

        if (runJobArguments.testRun) {
            grailsConsole.info "run-job url is ${url}"
        } else {
            def triggerId = restBuilder.get(url) {
                auth("admin", "password")
            }.text
            grailsConsole.info "triger id is: $triggerId"
        }
    }

}

def usage() {
    grailsConsole.info runJobUsage
}

def parseArgumentsAndSetDefaults() {
    runJobArguments.host = argsMap.host ? argsMap.host : "localhost:8080"
    runJobArguments.testRun = argsMap."test-run" ? true : false
    runJobArguments.protocol = argsMap.protocol ? argsMap.protocol : "http"
    runJobArguments.target = argsMap.target ? "&target=${argsMap.target}" : ""
}

setDefaultTarget(main)
