package metridoc.core

import metridoc.camel.GroovyRouteBuilder
import metridoc.camel.SqlPlusComponent
import metridoc.utils.CamelUtils
import org.apache.camel.CamelContext
import org.apache.camel.ProducerTemplate
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.impl.DefaultCamelContextNameStrategy
import org.apache.camel.spi.Registry
import org.slf4j.LoggerFactory

import java.util.concurrent.TimeUnit

import org.quartz.*

abstract class MetridocJob {
    private static final jobLogger = LoggerFactory.getLogger(MetridocJob)
    def concurrent = false
    def grailsApplication

    private static final MANUAL_RUN_ID_PREFIX = "manual-run"
    static final MANUAL_RUN_TRIGGER = {
        def uuid = UUID.randomUUID().toString()
        def twentyYears = TimeUnit.DAYS.toMillis(365 * 20)
        simple name: "$MANUAL_RUN_ID_PREFIX-$uuid", repeatInterval: 1000, startDelay: twentyYears
    }

    static final MIDNIGHT_TRIGGER = {
        def triggerName = it
        def trigger = {
            def params = [:]
            if (triggerName) {
                params.name = triggerName
            }
            params.cronExpression = "0 0 0 * * ?"
            cron params
        }

        if (triggerName) {
            return trigger
        } else {
            trigger.call()
        }
    }

    private final static ThreadLocal<ProducerTemplate> _producerJobTemplate = new ThreadLocal<ProducerTemplate>()

    private final static ThreadLocal<Registry> _camelJobRegistry = new ThreadLocal<Registry>()

    private final static ThreadLocal<CamelContext> _camelJobContext = new ThreadLocal<CamelContext>()

    private final static ThreadLocal<Map<String, Closure>> targetMap = new ThreadLocal<Map<String, Closure>>() {
        @Override
        protected Map initialValue() {
            [:] as Map<String, Closure>
        }
    }

    private final static ThreadLocal<Set> targetsRan = new ThreadLocal<Set>() {
        @Override
        protected Set initialValue() {
            [] as Set
        }
    }

    def execute(JobExecutionContext context) {
        try {
            refreshThreadLocalVariables()
            doExecute(context)
            doExecute()
            String targetFromJobDataMap = context?.trigger?.jobDataMap?.get("target")
            if (targetFromJobDataMap) {
                depends(targetFromJobDataMap)
            } else {
                def containsDefault = targetMap.get().containsKey("default")
                if (containsDefault) {
                    depends("default")
                }
            }
        } catch (Throwable t) {
            jobLogger.error("error occurred running job " + context.getJobDetail().getKey().getName() + " with trigger " + context.getTrigger().getKey().getName(), t);
            throw new JobExecutionException(t)
        } finally {
            try {
                getCamelJobContext().stop()
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

    def refreshThreadLocalVariables() {
        _producerJobTemplate.set(null)
        _camelJobContext.set(null)
        _camelJobRegistry.set(null)
        targetMap.set([:])
    }

    def runRoute(Closure closure) {
        def routeBuilder = new GroovyRouteBuilder()
        routeBuilder.setRoute(closure)
        runRoute(routeBuilder)
    }

    def runRoute(RouteBuilder builder) {
        getCamelJobContext().addRoutes(builder)
        CamelUtils.waitTillDone(getCamelJobContext())
    }

    def target(Map data, Closure closure) {
        if (data.size() != 1) {
            throw new JobExecutionException("the map in target can only have one variable, which is the name and the description of the target")
        }
        def key = (data.keySet() as List<String>)[0]
        targetMap.get().put(key, closure)
    }

    def depends(String... targetNames) {
        targetNames.each {targetName ->
            Closure target = targetMap.get().get(targetName)
            if (!target) {
                throw new JobExecutionException("target $targetName does not exist")
            }
            def targetHasNotBeenCalled = !targetsRan.get().contains(targetName)
            if (targetHasNotBeenCalled) {
                target.delegate = this
                target.resolveStrategy = Closure.DELEGATE_FIRST
                target.call()
                targetsRan.get().add(targetName)
            }
        }
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

    def includeTargets(Class<Script> scriptClass) {
        return includeTargets(scriptClass, new Binding())
    }

    def includeTargets(Class<Script> scriptClass, Binding binding) {

        binding.setVariable("target") {Map description, Closure closure ->
            target(description, closure)
        }
        Script script = scriptClass.newInstance()
        script.binding = binding
        script.run()

        return binding
    }

    def doExecute() {
        //override this
    }

    def doExecute(JobExecutionContext context) {
        //override this if job details are required
    }

    ProducerTemplate getProducerJobTemplate() {
        if (_producerJobTemplate.get()) return _producerJobTemplate.get()

        _producerJobTemplate.set(getCamelJobContext().createProducerTemplate())
        return _producerJobTemplate.get()
    }

    CamelContext getCamelJobContext() {
        if (_camelJobContext.get()) return _camelJobContext.get()
        def camelJobContext = new DefaultCamelContext(getCamelJobRegistry())
        camelJobContext.nameStrategy = new DefaultCamelContextNameStrategy("metridocCamelJob")
        camelJobContext.setLazyLoadTypeConverters(true)
        camelJobContext.addComponent("sqlplus", new SqlPlusComponent(camelJobContext))
        camelJobContext.start()
        _camelJobContext.set(camelJobContext)
        return _camelJobContext.get()
    }

    def getCamelJobRegistry() {
        if (_camelJobRegistry.get()) return _camelJobRegistry.get()
        final job = this
        def camelJobRegistry = new Registry() {

            @Override
            Object lookup(String s) {
                try {
                    return job."${s}"
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
                    jobLogger.warn "Could not convert object $s to ${tClass.name}, lookup will return null instead of the object value", ex
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

        _camelJobRegistry.set(camelJobRegistry)
        return _camelJobRegistry.get()
    }
}
