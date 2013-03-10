package metridoc.utils

import metridoc.core.JobDetails
import org.quartz.*
import org.quartz.impl.matchers.GroupMatcher

import static org.springframework.util.Assert.notNull

/**
 * provides various helpful utilities for quartz
 */
class QuartzUtils {

    static long NEXT_FIRE_TIME_WHERE_JOB_CONSIDERED_MANUAL = 1000L * 60L * 60L * 24L * 365L * 2L //TWO_YEARS

    static boolean isManual(Trigger trigger) {
        long nextFireTime = trigger.nextFireTime.time
        long timeToNextFire = nextFireTime - new Date().time
        boolean isManual = timeToNextFire > NEXT_FIRE_TIME_WHERE_JOB_CONSIDERED_MANUAL
        return isManual
    }

    static List<String> getTriggerSchedules() {
        def result = []
        JobTrigger.values().each {
            result << it.toString()
        }

        return result
    }

    static TriggerKey triggerJobFromJobName(Scheduler quartzScheduler, String jobName) {
        return triggerJobFromJobName(quartzScheduler, jobName, new JobDataMap())
    }

    static TriggerKey triggerJobFromJobName(Scheduler quartzScheduler, String jobName, JobDataMap dataMap) {
        def jobKey = new JobKey(jobName)
        List<org.quartz.Trigger> triggers = quartzScheduler.getTriggersOfJob(jobKey)
        if (triggers) {
            return triggerJobFromTrigger(quartzScheduler, triggers[0], dataMap)
        }

        quartzScheduler.triggerJob(jobKey, dataMap)
        return null
    }

    static TriggerKey triggerJobFromTriggerName(Scheduler quartzScheduler, String triggerName) {
        triggerJobFromTriggerName(quartzScheduler, triggerName, new JobDataMap())
    }

    static TriggerKey triggerJobFromTriggerName(Scheduler quartzScheduler, String triggerName, JobDataMap dataMap) {
        def triggerKey = new TriggerKey(triggerName)
        org.quartz.Trigger trigger = quartzScheduler.getTrigger(triggerKey)
        notNull(trigger, "Could not find job ${triggerName}")
        return triggerJobFromTrigger(quartzScheduler, trigger, dataMap)
    }

    static TriggerKey triggerJobFromTrigger(Scheduler quartzScheduler, org.quartz.Trigger trigger, JobDataMap dataMap) {
        notNull(trigger, "trigger cannot be null")
        def oldTrigger = trigger
        dataMap.oldTrigger = oldTrigger
        def newTrigger = getTriggerNowTrigger(trigger, dataMap)
        quartzScheduler.rescheduleJob(trigger.key, newTrigger)

        return newTrigger.key
    }

    static TriggerKey triggerJobFromTrigger(Scheduler quartzScheduler, org.quartz.Trigger trigger) {
        triggerJobFromTrigger(quartzScheduler, trigger, new JobDataMap())
    }

    static org.quartz.Trigger getTriggerNowTrigger(org.quartz.Trigger trigger, JobDataMap dataMap) {
        notNull(trigger, "trigger cannot be null")
        def jobKey = trigger.getJobKey()
        def schedule = SimpleScheduleBuilder.simpleSchedule().withIntervalInHours(1 * 24 * 356 * 4).repeatForever()
        def now = new Date()
        long fiveYears = 1000L * 60L * 60L * 24L * 365L * 5L + now.time
        def end = new Date(fiveYears)
        return TriggerBuilder.newTrigger().forJob(jobKey).startAt(now)
                .endAt(end).withIdentity(trigger.key).withSchedule(schedule).usingJobData(dataMap).build()
    }

    /**
     * gets a {@link metridoc.core.JobDetails} if it exists, otherwise returns a new one with the trigger name
     * @param triggerName
     * @return the job config associated with the trigger name
     */
    static JobDetails getJobDetailsByTrigger(String triggerName) {
        def jobDetails = JobDetails.findByJobName(triggerName)
        if (jobDetails) return jobDetails

        return new JobDetails(jobName: triggerName)
    }

    static org.quartz.Trigger getTriggerNowTrigger(org.quartz.Trigger trigger) {
        getTriggerNowTrigger(trigger, new JobDataMap())
    }

    static org.quartz.Trigger getTrigger(Scheduler quartzScheduler, String triggerName) {
        def triggerKey = new TriggerKey(triggerName)
        quartzScheduler.getTrigger(triggerKey)
    }

    static void eachTrigger(Scheduler quartzScheduler, Closure closure) {
        def listJobGroups = quartzScheduler.getJobGroupNames()
        listJobGroups?.each { jobGroup ->
            quartzScheduler.getJobKeys(GroupMatcher.groupEquals(jobGroup))?.each { jobKey ->
                def triggers = quartzScheduler.getTriggersOfJob(jobKey)
                triggers.each { Trigger trigger ->
                    closure.call(trigger)
                }
            }
        }
    }

    static Trigger searchForTrigger(Scheduler scheduler, String triggerName) {
        def triggerKey = new TriggerKey(triggerName)
        scheduler.getTrigger(triggerKey)
    }
}
