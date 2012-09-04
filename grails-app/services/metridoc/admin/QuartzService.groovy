package metridoc.admin

import grails.plugin.quartz2.InvokeMethodJob
import org.apache.commons.lang.StringUtils
import org.quartz.JobDataMap

import static org.quartz.JobBuilder.newJob
import static org.quartz.TriggerBuilder.newTrigger

import static org.quartz.TriggerKey.*
import grails.plugin.quartz2.SimpleJobDetail

class QuartzService {

    static final MAX_LIMIT = 100
    static final MAX_MINIMUM = 10
    def grailsApplication
    def triggersByName = [:]
    def quartzScheduler
    def jobDataByName = [:]
    def jobsByName = [:]
    def workflowsByName = [:]

    def scheduleJobs() {
        loadAllJobInfo()
        triggersByName.each {name, trigger ->
            quartzScheduler.scheduleJob(jobsByName[name], trigger)
        }
    }

    def loadAllJobInfo() {
        loadJobData()
        loadJobs()
        loadTriggers()
        loadWorkflows()
    }

    private loadWorkflows() {
        doWorkflowClassesIteration {name, grailsClass ->
            workflowsByName[name] = grailsClass
        }
    }

    private loadTriggers() {
        doWorkflowClassesIteration {name ->
            def schedule = grailsApplication.config.metridoc.scheduling.workflows."$name".schedule
            def startNow = grailsApplication.config.metridoc.scheduling.workflows."$name".startNow

            if (schedule) {

                def triggerBuilder = newTrigger()
                        .withIdentity("${name}Trigger", "Workflow").withSchedule(schedule)
                if (startNow) {
                    triggerBuilder = triggerBuilder.startNow()
                }
                triggersByName[name] = triggerBuilder.build()
            }
        }
    }

    private doWorkflowClassesIteration(Closure closure) {
        workflowClasses.each {
            def paramsCount = closure.maximumNumberOfParameters
            def unCapName = StringUtils.uncapitalise(it.name)
            if (paramsCount == 2) {
                closure.call(unCapName, it)
            } else {
                closure.call(unCapName)
            }
        }
    }

    private loadJobs() {
        doWorkflowClassesIteration {unCapName ->
            def jobDetail = new SimpleJobDetail()
            jobDetail.setJobData(jobDataByName[unCapName])
            jobDetail.concurrent = false
            jobDetail.name = "${unCapName}Job"
            jobDetail.group = "Worflow"
            jobDetail.jobClass = InvokeMethodJob

            jobsByName[unCapName] = jobDetail
        }
    }

    private loadJobData() {
        doWorkflowClassesIteration {unCapName, grailsClass ->
            jobDataByName[unCapName] = new JobDataMap(
                    [targetObject: grailsClass, targetMethod: "run"]
            )
        }
    }

    private getWorkflowClasses() {
        grailsApplication.workflowClasses
    }

    def listWorkflows(params) {
        def workflows = []

        doWorkflowClassesIteration {unCapName, grailsClass ->
            def workflowModel = [name: "$grailsClass.name", unCapName: unCapName]
            workflows << workflowModel
            loadJobDetails(grailsClass, workflowModel)
        }

        return listWorkflowsWithOffsetAndMax(params, workflows)
    }

    def totalWorkflowCount() {
        workflowClasses.size()
    }

    private getMax(params) {
        def max = MAX_MINIMUM

        def paramMax = params.max
        if (paramMax) {
            try {
                max = Integer.valueOf(paramMax)
            } catch (Exception ex) {
                log.warn("Could not convert max parameter ${paramMax} to integer")
            }
        }

        max < MAX_LIMIT ? max : MAX_LIMIT
    }

    private loadJobDetails(workflowClass, workflowModel) {
        def name = StringUtils.uncapitalise(workflowClass.name)
        def trigger = quartzScheduler.getTrigger(triggerKey("${name}Trigger", "Workflow"))

        workflowModel.previousFireTime = "NA"
        workflowModel.nextFireTime = "NA"
        workflowModel.endTime = "NA"
        workflowModel.previousDuration = "NA"

        if (trigger) {
            workflowModel.nextFireTime = trigger.nextFireTime.format("yyyy/MM/dd hh:mm:ss")
        }

        def previousFireTime = workflowClass.previousFireTime
        if (previousFireTime) {
            workflowModel.previousFireTime = previousFireTime.format("yyyy/MM/dd hh:mm:ss")
        }

        def endTime = workflowClass.previousEndTime
        if (endTime) {
            workflowModel.endTime = endTime.format("yyyy/MM/dd hh:mm:ss")
        }

        def previousDuration = workflowClass.previousDuration
        if(previousDuration) {
            workflowModel.previousDuration = previousDuration
        }

        workflowModel.running = workflowClass.running
        workflowModel.lastException = workflowClass.lastException
    }

    private listWorkflowsWithOffsetAndMax(params, workflows) {
        def ordered = listOrderedWorkflows(params, workflows)
        def offset = 0
        try {
            offset = Integer.valueOf(params.offset)
        } catch (Exception ex) {
            //do nothing
        }

        def to = Math.min(getMax(params) + offset, workflows.size())

        ordered.subList(offset, to)
    }

    private static listOrderedWorkflows(params, workflows) {
        def result = []
        def order = params.order

        if (order) {
            def map = new TreeMap()
            workflows.each {
                map.put(it.name, it)
            }

            result.addAll map.values()

            if (order == "desc") {
                result = result.reverse()
            }
        } else {
            result = workflows
        }

        return result
    }
}
