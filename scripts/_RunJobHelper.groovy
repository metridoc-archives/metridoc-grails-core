import org.quartz.JobDataMap
import org.quartz.Scheduler

includeTargets << grailsScript("_GrailsInit")

doCallFromAppCtx = {
    def initQuartzService = appCtx.metridocCoreInitQuartzService
    initQuartzService.initializeScheduler()
    def quartzService = appCtx.metridocCoreQuartzService
    def jobName = argsMap.params[0]
    quartzService.runCliJob(jobName, args)

    grailsConsole.info "Finished running job ${jobName}"
}

