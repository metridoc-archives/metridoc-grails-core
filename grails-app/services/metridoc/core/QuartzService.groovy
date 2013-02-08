package metridoc.core

import org.quartz.*

import static org.springframework.util.Assert.notNull

class QuartzService {

    def quartzScheduler

    TriggerKey triggerJobFromTriggerName(String triggerName) {
        triggerJobFromTriggerName(triggerName, new JobDataMap())
    }

    TriggerKey triggerJobFromTriggerName(String triggerName, JobDataMap dataMap) {
        def triggerKey = new TriggerKey(triggerName)
        def trigger = quartzScheduler.getTrigger(triggerKey)
        notNull(trigger, "Could not find job ${triggerName}")
        return triggerJobFromTrigger(trigger, dataMap)
    }

    TriggerKey triggerJobFromTrigger(Trigger trigger, JobDataMap dataMap) {
        notNull(trigger, "trigger cannot be null")
        def oldTrigger = trigger
        dataMap.oldTrigger = oldTrigger
        def newTrigger = getTriggerNowTrigger(trigger, dataMap)
        quartzScheduler.rescheduleJob(trigger.key, newTrigger)

        return newTrigger.key
    }

    TriggerKey triggerJobFromTrigger(Trigger trigger) {
        triggerJobFromTrigger(trigger, new JobDataMap())
    }

    Trigger getTriggerNowTrigger(Trigger trigger, JobDataMap dataMap) {
        notNull(trigger, "trigger cannot be null")
        def jobKey = trigger.getJobKey()
        def schedule = SimpleScheduleBuilder.simpleSchedule().withIntervalInHours(1 * 24 * 56 * 4).repeatForever()
        def now = new Date()
        def end = now + 365 * 5 //5 years in the future
        return TriggerBuilder.newTrigger().forJob(jobKey).startAt(new Date())
                .endAt(end).withIdentity(trigger.key).withSchedule(schedule).usingJobData(dataMap).build()
    }

    Trigger getTriggerNowTrigger(Trigger trigger) {
        getTriggerNowTrigger(trigger, new JobDataMap())
    }

    Trigger getTrigger(String triggerName) {
        def triggerKey = new TriggerKey(triggerName)
        quartzScheduler.getTrigger(triggerKey)
    }
}
