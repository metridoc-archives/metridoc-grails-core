package metridoc.core

import org.apache.camel.builder.RouteBuilder
import org.quartz.JobExecutionContext

import org.apache.camel.ProducerTemplate
import org.apache.camel.CamelContext
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.spi.Registry
import metridoc.camel.GroovyRouteBuilder
import org.apache.camel.impl.DefaultCamelContextNameStrategy
import metridoc.camel.SqlPlusComponent
import metridoc.utils.CamelUtils
import org.quartz.JobExecutionException

abstract class MetridocJob {

    def concurrent = false
    def grailsApplication

    private final static ThreadLocal<ProducerTemplate> _producerJobTemplate = new ThreadLocal<ProducerTemplate>()

    private final static ThreadLocal<ProducerTemplate> _camelJobRegistry = new ThreadLocal<Registry>()

    private final static ThreadLocal<CamelContext> _camelJobContext = new ThreadLocal<CamelContext>()

    private final static ThreadLocal<Map> targetMap = new ThreadLocal<Map>() {
        @Override
        protected Map initialValue() {
            [:]
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
            def targetFromJobDataMap = context.trigger.jobDataMap.get("target")
            if(targetFromJobDataMap) {
                depends(targetFromJobDataMap)
            } else {
                def containsDefault = targetMap.get().containsKey("default")
                if(containsDefault) {
                    depends("default")
                }
            }
        } catch (Throwable t) {
            log.error("error occurred running job " + context.getJobDetail().getKey() + " with trigger " + context.getTrigger().getKey(), t);
            throw new JobExecutionException(t)
        } finally {
            try {
                getCamelJobContext().stop()
            } catch (Throwable throwable) {
                //ignore
                log.warn("exception while shutting down camel", throwable)
            }
        }
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
        if(data.size() != 1) {
            throw new JobExecutionException("the map in target can only have one variable, which is the name and the description of the target")
        }
        def key = (data.keySet() as List)[0]
        targetMap.get().put(key, closure)
    }

    def depends(String... targetNames) {
        targetNames.each{targetName ->
            Closure target = targetMap.get().get(targetName)
            if(!target) {
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
        log.info "profiling [$description] start"
        closure.call()
        def end = System.currentTimeMillis()
        log.info "profiling [$description] finished ${end - start} ms"
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
                    return job.grailsApplication.mainContext."${s}"
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
