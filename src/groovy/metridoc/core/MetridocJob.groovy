package metridoc.core

import metridoc.camel.CamelScript
import metridoc.camel.CamelScriptRegistry
import metridoc.camel.GroovyRouteBuilder
import metridoc.camel.SqlPlusComponent
import org.apache.camel.CamelContext
import org.apache.camel.ProducerTemplate
import org.apache.camel.ServiceStatus
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.impl.DefaultCamelContextNameStrategy
import org.apache.camel.spi.Registry
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

    private static final MANUAL_RUN_ID_PREFIX = "manual-run"

    /**
     * unfortunately for quartz to actually register a job, it must have some fire time in the future.  This trigger is
     * meant to represent a job that is meant to be manually fired off in the scheduling view.  To get quartz to
     * register the job, t is actually a simple trigger that will wait an arbitrarily long time to fire off, in this
     * case 20 years.  This will reset everytime the application is restarted
     */
    static final MANUAL_RUN_TRIGGER = {
        def uuid = UUID.randomUUID().toString()
        def twentyYears = TimeUnit.DAYS.toMillis(365 * 20)
        simple name: "$MANUAL_RUN_ID_PREFIX-$uuid", repeatInterval: 1000, startDelay: twentyYears
    }

    /**
     * represents a midnight trigger
     */
    static final MIDNIGHT = {
        cron cronExpression: "0 0 0 * * ?"
    }

    private ProducerTemplate _producerJobTemplate
    private Registry _camelJobRegistry
    private CamelContext _camelJobContext
    def Map targetMap = Collections.synchronizedMap([:])
    private Set _targetsRan = [] as Set
    def Map jobDataMap = [:]

    /**
     * necessary method that the grails job artifact that quartz will trigger
     *
     * @param context
     * @return
     */
    def execute(JobExecutionContext context) {
        try {
            jobDataMap = context?.trigger?.jobDataMap
            doExecute(context)
            doExecute()
            String targetFromJobDataMap = context?.trigger?.jobDataMap?.get("target")
            if (targetFromJobDataMap) {
                depends(targetFromJobDataMap)
            } else {
                jobLogger.info("target map is $targetMap")
                def containsDefault = targetMap.containsKey("default")
                if (containsDefault) {
                    jobLogger.debug "running default"
                    depends("default")
                }
            }
        } catch (Throwable t) {
            jobLogger.error("error occurred running job " + context.getJobDetail().getKey().getName() + " with trigger " + context.getTrigger().getKey().getName(), t);
            throw new JobExecutionException(t)
        } finally {
            try {
                if (_camelJobContext && _camelJobContext.status == !ServiceStatus.Stopped) {
                    getCamelJobContext().stop()
                }
            } catch (Throwable throwable) {
                //ignore
                jobLogger.warn("exception while shutting down camel", throwable)
            }
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
        def start = System.currentTimeMillis()
        jobLogger.info "profiling [$description] start"
        closure.call()
        def end = System.currentTimeMillis()
        jobLogger.info "profiling [$description] finished ${end - start} ms"
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

    /**
     * eventually this will be turned into an abstract method
     */
    def doExecute() {
        //override this
    }

    /**
     *
     * since each execution is in a new job and the job data map is stored, this is completely unnecessary
     *
     * @deprecated will be deprecated in the future so we can just run doExecute
     */
    def doExecute(JobExecutionContext context) {
        //override this if job details are required
    }

    synchronized ProducerTemplate getProducerJobTemplate() {
        if (_producerJobTemplate) return _producerJobTemplate

        _producerJobTemplate = getCamelJobContext().createProducerTemplate()
    }

    synchronized CamelContext getCamelJobContext() {
        if (_camelJobContext) return _camelJobContext
        def camelJobContext = new DefaultCamelContext(getCamelJobRegistry())
        camelJobContext.nameStrategy = new DefaultCamelContextNameStrategy("metridocCamelJob")
        camelJobContext.setLazyLoadTypeConverters(true)
        camelJobContext.addComponent("sqlplus", new SqlPlusComponent(camelJobContext))
        camelJobContext.start()
        _camelJobContext = camelJobContext
    }

    synchronized Registry getCamelJobRegistry() {
        if (_camelJobRegistry) return _camelJobRegistry
        final job = this
        def camelJobRegistry = new Registry() {

            @Override
            Object lookup(String s) {
                try {
                    def propertyValue = job."${s}"

                    return propertyValue
                } catch (MissingPropertyException e) {
                    jobLogger.debug "Job is missing property $s, will search for property in application context", e
                    return job.grailsApplication?.mainContext?."${s}"
                }
            }

            @Override
            def <T> T lookup(String s, Class<T> tClass) {
                Object o = lookup(s);

                try {
                    if (o) {
                        return tClass.cast(o);
                    }
                } catch (ClassCastException ex) {
                    jobLogger.debug "Could not convert object $s to ${tClass.name}, lookup will return null instead of the object value", ex
                }

                return null
            }

            @Override
            def <T> Map<String, T> lookupByType(Class<T> tClass) {
                def result = [:]
                job.metaClass.properties.each {
                    def propertyName = it.getName()
                    def lookup = lookup(propertyName, tClass)
                    if (lookup) {
                        result[propertyName] = lookup
                    }
                }

                return result;
            }
        }

        _camelJobRegistry = camelJobRegistry
    }

}
