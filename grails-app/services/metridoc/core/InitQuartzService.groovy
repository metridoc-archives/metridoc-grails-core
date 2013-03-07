package metridoc.core

import grails.plugin.quartz2.GrailsJobClass
import grails.plugin.quartz2.SimpleJobDetail
import grails.plugin.quartz2.TriggersBuilder
import org.quartz.JobKey
import org.quartz.Scheduler
import org.quartz.Trigger
import org.quartz.TriggerKey

import java.util.concurrent.TimeUnit

import static metridoc.utils.JobTrigger.IN_CODE
import static metridoc.utils.JobTrigger.NEVER

class InitQuartzService {

    Scheduler quartzScheduler
    def quartzService
    def grailsApplication
    def pluginManager

    def initializeScheduler() {
        def disableQuartz = Boolean.valueOf(System.getProperty("metridoc.quartz.disabled", "false"))

        if (disableQuartz) {
            //handy for command line jobs via run-job
            quartzScheduler.shutdown(false)
            return
        }

        //just in case it is running even though it shouldn't
        def doResume = false
        if (quartzScheduler.isStarted()) {
            quartzScheduler.pauseAll()
            doResume = true
        }
        handleUnscheduledCodeJobs()
        handleAlreadyScheduledCodeJobs()
        handleUrlBasedScripts()
        //everything is ready to go!
        if (doResume) {
            quartzScheduler.resumeAll()
        } else {
            quartzScheduler.start()
        }
    }

    def handleUnscheduledCodeJobs() {
        def applicationContext = grailsApplication.mainContext
        def quartzPlugin = pluginManager.getGrailsPluginForClassName("Quartz2GrailsPlugin").instance
        grailsApplication.jobClasses.each { GrailsJobClass jobClass ->
            if (!jobClass.triggers) {
                Closure scheduleJob = quartzPlugin.scheduleJob
                scheduleJob.delegate = quartzPlugin
                TriggersBuilder builder = new TriggersBuilder(jobClass.fullName)

                builder.build(MetridocJob.MANUAL_RUN_TRIGGER)
                def triggers = (Map) builder.getTriggers()
                jobClass.triggers.putAll(triggers)
                scheduleJob.call(jobClass, applicationContext, quartzScheduler)
            }
        }
    }

    def handleAlreadyScheduledCodeJobs() {
        quartzService.eachTrigger {Trigger trigger ->
            def key = trigger.key
            String jobName = key.name
            def details = JobDetails.findByJobName(jobName)

            if (details) {
                def jobTrigger = details.jobTrigger
                if (jobTrigger != IN_CODE) {
                    Trigger newTrigger = details.convertTriggerToQuartzTrigger()
                    newTrigger.key = key
                    //right now
                    newTrigger.startTime = new Date()
                    if (jobTrigger == NEVER) {
                        long fiftyYears = TimeUnit.DAYS.toMillis(365 * 50)
                        newTrigger.startTime = new Date(new Date().time + fiftyYears)
                    }

                    quartzScheduler.rescheduleJob(key, newTrigger)
                }
            } else {
                //let's create an entry then
                def detail = new JobDetails()
                detail.jobTrigger = IN_CODE
                detail.classToRun = trigger.jobKey.name
                detail.jobName = jobName
                detail.template = trigger.jobKey.name
                detail.save()
            }
        }
    }

    def handleUrlBasedScripts() {
        def details = JobDetails.where {
            url != null
        }
        details.each {
            def trigger = it.convertTriggerToQuartzTrigger()
            trigger.key = new TriggerKey(it.jobName)
            if (!quartzScheduler.getTrigger(trigger.key)) {
                trigger.jobName = it.template
                trigger.startTime = new Date()
                if (it.jobTrigger == NEVER) {
                    long fiftyYears = TimeUnit.DAYS.toMillis(365 * 50)
                    trigger.startTime = new Date(new Date().time + fiftyYears)
                }
                SimpleJobDetail quartzDetails = new SimpleJobDetail()
                quartzDetails.key = new JobKey(it.template)
                quartzScheduler.addJob(quartzDetails, true)
                quartzScheduler.scheduleJob(trigger)
            }
        }
    }
}
