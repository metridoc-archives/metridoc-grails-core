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
    static final log = LoggerFactory.getLogger(MetridocJob)

    def execute(JobExecutionContext context) {
        doExecute(context)
        doExecute()
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

    }

    def dependsOn(String targetName) {

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
        if(producerJobTemplate) return producerJobTemplate

        producerJobTemplate = getCamelJobContext().createProducerTemplate()
    }

    def getCamelJobContext() {
        if(camelJobContext) return camelJobContext
        camelJobContext = new DefaultCamelContext(getCamelJobRegistry())
        camelJobContext.nameStrategy = new DefaultCamelContextNameStrategy("metridocCamelJob")
        camelJobContext.setLazyLoadTypeConverters(true)
        camelJobContext.addComponent("sqlplus", new SqlPlusComponent(camelJobContext))
        camelJobContext.start()

        return camelJobContext
    }

    def getCamelJobRegistry() {
        if(camelJobRegistry) return camelJobRegistry
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
                    if(lookup) {
                        result[propertyName] = lookup
                    }
                }

                return result;
            }
        }
    }
}
