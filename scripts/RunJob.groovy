import grails.util.Holders

/*
 runs a job restfully.  By default it constructs a url to local host with user name admin and password password.
*/
includeTargets << grailsScript("_GrailsBootstrap")
includeTargets << new File("$metridocCorePluginDir/scripts/_RunJobHelper.groovy")

runJobUsage = """
////////////////////////////////
// RUN-JOB USAGE              //
////////////////////////////////

grails run-job [options] <job name>

Options:

         h: displays this message
"""

runJobArguments = [:]

target(main: "runs a metridoc job via grails commandline") {
    depends(parseArguments)
    System.setProperty("metridoc.email.disabled", "true")
    System.setProperty("metridoc.job.cliOnly", "true")
    if (argsMap.h) {
        usage()
    } else if (argsMap.params.size != 1) {
        grailsConsole.error "1 job must be specified, run grails run-job -h for more info"
    } else {
        boolean initializeScheduler = false
        try {
            appCtx = Holders.applicationContext
            grailsConsole.info "running the job from the already running application"
        } catch (Exception e) {
            grailsConsole.info "running as a commandline job"
            depends(bootstrap)
            initializeScheduler = true
        }

        doCallFromAppCtx(initializeScheduler)
    }
}

def usage() {
    grailsConsole.info runJobUsage
}

setDefaultTarget(main)
