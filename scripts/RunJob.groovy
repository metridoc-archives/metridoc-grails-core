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
        if(!argsMap.email) {
            System.setProperty("metridoc.email.disabled", "true")
        }
        depends(packageApp, loadApp, configureApp)
        includeTargets << new File("$metridocCorePluginDir/scripts/_RunJobHelper.groovy")
        doCliCall()
    } else {
        depends(packageApp)
        includeTargets << new File("$metridocCorePluginDir/scripts/_RunJobHelper.groovy")
        if (!doRestCall()) {
            System.setProperty("metridoc.quartz.disabled", "true")
            System.setProperty("metridoc.job.cliOnly", "true")
            depends(loadApp, configureApp)
            doCliCall()
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
