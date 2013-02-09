import org.quartz.JobDataMap
import org.quartz.Scheduler

includeTargets << grailsScript("_GrailsInit")

doCallFromAppCtx = {
    def jobDataMap = new JobDataMap(argsMap)
    def jobName = argsMap.params[0]
    Scheduler scheduler = appCtx.quartzScheduler
    def job = appCtx."$jobName"
    try {
        job.jobDataMap = jobDataMap
    } catch (MissingPropertyException ex) {
        //ignore
    }
    try {
        job.execute(null)
    } catch (MissingPropertyException e) {
        //ignore and try execute
        job.execute()
    }
    grailsConsole.info "Finished running job ${jobName}"
}

