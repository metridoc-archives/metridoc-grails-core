import org.springframework.context.ApplicationContext

includeTargets << grailsScript("_GrailsInit")



doCallFromAppCtx = {

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

