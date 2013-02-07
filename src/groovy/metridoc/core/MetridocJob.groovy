package metridoc.core

import metridoc.camel.CamelScript
import metridoc.camel.CamelScriptRegistry
import metridoc.camel.GroovyRouteBuilder
import metridoc.camel.SqlPlusComponent
import org.apache.camel.builder.RouteBuilder
import org.apache.commons.lang.exception.ExceptionUtils
import org.quartz.*
import org.slf4j.LoggerFactory

import java.util.concurrent.TimeUnit

/**
 * This is the primary class used for running MetriDoc jobs.  Although unnecessary to actually create a periodic job,
 * it contains helpful methods for routing and profiling tasks, in addition to common triggers.
 */
abstract class MetridocJob {

    private static final jobLogger = LoggerFactory.getLogger(MetridocJob)
    /**
     * all metridoc jobs are not concurrent by default.  Any needed concurrency should be incorporated into the job \
     * itself
     */
    def concurrent = false
    def grailsApplication
    def mailService
    def commonService

    /**
     * unfortunately for quartz to actually register a job, it must have some fire time in the future.  This trigger
     * has an arbitrarily long start up delay (50 years).  By doing this the job can be managed via the quartzscheduler.
     */
    static final MANUAL_RUN_TRIGGER = {
        def fiftyYears = TimeUnit.DAYS.toMillis(365 * 50)
        simple repeatInterval: 1000, startDelay: fiftyYears
    }

    /**
     * represents a midnight trigger
     */
    static final MIDNIGHT = {
        cron cronExpression: "0 0 0 * * ?"
    }

    def Map<String, Closure> targetMap = Collections.synchronizedMap([:])
    private Set _targetsRan = [] as Set
    def Map jobDataMap = [:]
    boolean interrupted

    /**
     * necessary method that the grails job artifact that quartz will trigger
     *
     * @param context
     * @return
     */
    def execute(JobExecutionContext context) {
        try {
            jobDataMap = context?.trigger?.jobDataMap
            doExecute()
            String targetFromJobDataMap = context?.trigger?.jobDataMap?.get("target")
            if (targetFromJobDataMap) {
                depends(targetFromJobDataMap)
            } else {
                jobLogger.debug("target map is $targetMap")
                def containsDefault = targetMap.containsKey("default")
                if (containsDefault) {
                    jobLogger.debug "running default"
                    depends("default")
                }
            }
        } catch (Throwable t) {
            String jobName = context.getJobDetail().getKey().getName()
            String shortErrorMessage = "error occurred running job ${jobName} with trigger " + context.getTrigger().getKey().getName() + " will notify interested users by email"
            //TODO: maybe we don't need to do this?  Does quartz already handle this?
            jobLogger.error(shortErrorMessage, t);

            def emails = NotificationEmails.findByScope(QuartzController.JOB_FAILURE_SCOPE).collect { it.email }

            def emailIsConfigured = commonService.emailIsConfigured() && emails && !NotificationEmailsService.emailDisabledFromSystemProperty()
            if (emailIsConfigured) {
                emails.each { email ->
                    try {
                        jobLogger.info "sending email to ${email} about ${jobName} failure"
                        mailService.sendMail {
                            subject shortErrorMessage
                            text ExceptionUtils.getFullStackTrace(t)
                            to email
                        }
                    } catch (Throwable throwable) {
                        jobLogger.error("could not send email to ${email}", throwable)
                    }
                }

            } else {
                jobLogger.info "could not send email about ${jobName} failure since email is not configured or is disabled"
            }
            throw new JobExecutionException(t)
        }
    }


    def executeTarget() {
        execute(buildJobContextFacade())
    }

    def executeTarget(String target) {
        execute(buildJobContextFacade(target))
    }

    /**
     * creates a fake ${@link JobExecutionContext} to help with command line only jobs
     *
     * @param target
     * @return
     */
    JobExecutionContext buildJobContextFacade(String target = null) {
        [
                getJobDetail: {
                    [
                            getKey: {
                                new JobKey(this.class.name)
                            }
                    ] as JobDetail
                },
                getTrigger: {
                    [
                            getKey: {
                                new TriggerKey("manual-cli")
                            },
                            getJobDataMap: {
                                def jobDataMap = new JobDataMap()
                                jobDataMap.put("target", target)
                                return jobDataMap
                            }
                    ] as Trigger
                }

        ] as JobExecutionContext
    }

    /**
     * runs the closure as a route within the {@link GroovyRouteBuilder}.  Really handy for database migrations and
     * batch processing
     *
     * @param closure
     * @return
     */
    def runRoute(Closure closure) {
        CamelScript.components.put("sqlplus", SqlPlusComponent)
        closure.delegate = this
        CamelScript.runRoute(closure)
    }

    /**
     * instead of using runRoute with a closure, you can pass a RouteBuilder instead.  This can be convenient if someone
     * wants to use their own {@link RouteBuilder} or wants to benefit from code completion
     *
     * @param builder
     */
    def runRoute(RouteBuilder builder) {
        CamelScript.components.put("sqlplus", SqlPlusComponent)
        def mockClosure = {}
        mockClosure.delegate = builder
        def registry = new CamelScriptRegistry(closure: mockClosure)
        CamelScript.runRouteBuilders(registry, builder)
    }

    /**
     * creates a target for later execution.  This idea was taken directly from the grails / GANT system and works
     * similarily
     *
     * @param data
     * @param closure
     * @return
     */
    def target(Map data, Closure closure) {
        closure.delegate = this //required for imported targets
        if (data.size() != 1) {
            throw new JobExecutionException("the map in target can only have one variable, which is the name and the description of the target")
        }
        def key = (data.keySet() as List<String>)[0]
        def description = data[key]
        def closureToRun = {
            profile(description, closure)
        }
        targetMap.put(key, closureToRun)
    }

    /**
     * fires off a target by name if it has not been run yet.  If it has run then it is skipped
     *
     * @param targetNames
     * @return
     */
    def depends(String... targetNames) {
        targetNames.each { targetName ->
            Closure target = targetMap.get(targetName)
            if (!target) {
                throw new JobExecutionException("target $targetName does not exist")
            }
            def targetHasNotBeenCalled = !targetsRan.contains(targetName)
            if (targetHasNotBeenCalled) {
                target.delegate = this
                target.resolveStrategy = Closure.DELEGATE_FIRST
                target.call()
                targetsRan.add(targetName)
            }
        }
    }

    /**
     *
     * @return all targets that have run
     */
    Set getTargetsRan() {
        return _targetsRan
    }
    /**
     * profiles a chunk of code stating when it starts and finishes
     * @param description description of the chunk of code
     * @param closure the code to run
     */
    def profile(String description, Closure closure) {
        if (interrupted) {
            throw new JobInteruptionException(this.getClass().name)
        }
        def start = System.currentTimeMillis()
        jobLogger.info "profiling [$description] start"
        closure.call()
        def end = System.currentTimeMillis()
        jobLogger.info "profiling [$description] finished ${end - start} ms"
        if (interrupted) {
            throw new JobInteruptionException(this.getClass().name)
        }
    }

    /**
     * loads scripts that contain targets to allow for code reuse
     *
     * @param scriptClass
     * @return returns the binding from the script in case global variables need to accessed
     */
    def includeTargets(Class<Script> scriptClass) {
        return includeTargets(scriptClass, new Binding())
    }

    /**
     * the same as {@link #includeTargets(java.lang.Class)}, but a binding can be passed so more global variables can
     * be loaded
     *
     * @param scriptClass
     * @param binding
     * @return the passed binding
     */
    def includeTargets(Class<Script> scriptClass, Binding binding) {

        binding.setVariable("target") { Map description, Closure closure ->
            target(description, closure)
        }
        Script script = scriptClass.newInstance()
        script.binding = binding
        script.run()

        return binding
    }

    abstract doExecute()

    void interrupt() {
        interrupted = true
    }
}
