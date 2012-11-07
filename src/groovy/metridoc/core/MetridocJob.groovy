package metridoc.core

import org.apache.camel.builder.RouteBuilder
import org.quartz.JobExecutionContext

import org.slf4j.LoggerFactory
import org.apache.camel.ProducerTemplate
import org.apache.camel.CamelContext
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.spi.Registry
import metridoc.camel.GroovyRouteBuilder
import org.apache.camel.impl.DefaultCamelContextNameStrategy
import metridoc.camel.SqlPlusComponent
import metridoc.utils.CamelUtils

abstract class MetridocJob {

    def concurrent = false
    def grailsApplication
    ProducerTemplate producerJobTemplate
    Registry camelJobRegistry
    CamelContext camelJobContext

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
        doExecute(context)
        doExecute()
        def targetFromJobDataMap = context.jobDetail.jobDataMap.get("target")
        if(targetFromJobDataMap) {
            dependsOn(targetFromJobDataMap)
        } else {
            def containsDefault = targetMap.get().containsKey("default")
            if(containsDefault) {
                dependsOn("default")
            }
        }

        getCamelJobContext().stop()
        camelJobContext = null
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
        assert data.size() == 1: "the map in target can only have one variable, which is the name and the description of the target"
        def key = (data.keySet() as List)[0]
        targetMap.get().put(key, closure)
    }

    def dependsOn(String... targetNames) {
        targetNames.each{targetName ->
            Closure target = targetMap.get().get(targetName)
            assert target: "target $targetName does not exist"
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

    def includeTargets(Script script) {

    }

    def doExecute() {
        //override this
    }

    def doExecute(JobExecutionContext context) {
        //override this if job details are required
    }

    ProducerTemplate getProducerJobTemplate() {
        if (producerJobTemplate) return producerJobTemplate

        producerJobTemplate = getCamelJobContext().createProducerTemplate()
    }

    def getCamelJobContext() {
        if (camelJobContext) return camelJobContext
        camelJobContext = new DefaultCamelContext(getCamelJobRegistry())
        camelJobContext.nameStrategy = new DefaultCamelContextNameStrategy("metridocCamelJob")
        camelJobContext.setLazyLoadTypeConverters(true)
        camelJobContext.addComponent("sqlplus", new SqlPlusComponent(camelJobContext))
        camelJobContext.start()

        return camelJobContext
    }

    def getCamelJobRegistry() {
        if (camelJobRegistry) return camelJobRegistry
        final job = this
        camelJobRegistry = new Registry() {

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
    }
}
