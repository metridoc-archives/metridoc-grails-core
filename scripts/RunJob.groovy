import grails.util.Holders

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
        doCallFromAppCtx(appCtx)
    } else {
        depends(packageApp)
        includeTargets << new File("$metridocCorePluginDir/scripts/_RunJobHelper.groovy")
        def holderContext
        try {
            holderContext = Holders.applicationContext
        } catch (Exception e) {

        }
        if (!holderContext) {
            System.setProperty("metridoc.quartz.disabled", "true")
            System.setProperty("metridoc.job.cliOnly", "true")
            depends(loadApp, configureApp)
            holderContext = appCtx
        }

        doCallFromAppCtx(appCtx)
    }

}

def usage() {
    grailsConsole.info runJobUsage
}

def parseArgumentsAndSetDefaults() {
    runJobArguments.target = argsMap.target
    runJobArguments.job = argsMap.params ? argsMap.params[0] : null
}



setDefaultTarget(main)
