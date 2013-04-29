package metridoc.core

import grails.plugin.quartz2.GrailsJobClass
import grails.plugin.quartz2.SimpleJobDetail
import grails.plugin.quartz2.TriggersBuilder
import grails.util.Environment
import org.apache.commons.lang.StringUtils
import org.quartz.JobKey
import org.quartz.Scheduler
import org.quartz.Trigger
import org.quartz.TriggerKey

import java.util.concurrent.TimeUnit

import static metridoc.utils.JobTrigger.IN_CODE
import static metridoc.utils.JobTrigger.NEVER

class InitQuartzService {

    public static final String CLI_ONLY = "metridoc.job.cliOnly"
    Scheduler quartzScheduler
    def quartzService
    def grailsApplication
    def pluginManager

    def initializeScheduler() {
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
            startSchedulerIfNotACLIJob()
        }
    }

    def startSchedulerIfNotACLIJob() {
        if (Environment.current != Environment.TEST) {
            def args = System.getProperty("grails.cli.args")
            def isCliJob = Boolean.valueOf(System.getProperty(CLI_ONLY, "false"))
            if (!isCliJob) {
                log.info "quartz scheduler is starting"
                quartzScheduler.start()
            } else {
                log.info "quartz scheduler will not start since the job is being run as a command line job or tests are being run"
            }
        } else {
            log.info "quartz scheduler will not start since we are running a test"
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
                def neverRunTrigger = {
                    def fiftyYears = TimeUnit.DAYS.toMillis(365 * 50)
                    simple name: jobClass.fullName, repeatInterval: 1000, startDelay: fiftyYears
                }
                builder.build(neverRunTrigger)
                def triggers = (Map) builder.getTriggers()
                jobClass.triggers.putAll(triggers)
                scheduleJob.call(jobClass, applicationContext, quartzScheduler)
            }
        }
    }

    def handleAlreadyScheduledCodeJobs() {
        quartzService.eachTrigger { Trigger trigger ->
            def key = trigger.key
            String jobName = key.name
            if (jobName.endsWith("0")) {
                quartzScheduler.unscheduleJob(key)
                jobName = StringUtils.chop(jobName)
                trigger.key = new TriggerKey(jobName)
                quartzScheduler.scheduleJob(trigger)
            }
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
