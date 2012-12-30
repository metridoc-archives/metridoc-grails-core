package metridoc.camel

import groovy.util.logging.Slf4j
import metridoc.utils.CamelUtils
import org.apache.camel.Component
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.impl.SimpleRegistry
import org.apache.camel.spi.Registry
import org.apache.commons.lang.ObjectUtils

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 12/28/12
 * Time: 12:10 PM
 * To change this template use File | Settings | File Templates.
 */
@Slf4j
class CamelScript {

    static Map<String, Class<Component>> components = Collections.synchronizedMap([:])
    static RouteBuilder runRoute(Closure closure) {
        runRoute(null, closure)
    }

    static RouteBuilder runRoute(String start, Closure closure) {
        def routeBuilder = new GroovyRouteBuilder(route: closure)
        def registry = new CamelScriptRegistry(closure: closure)
        runRouteBuilders(start, registry, routeBuilder)

        return routeBuilder
    }

    static void runRouteBuilders(Map<String, Object> registry, RouteBuilder... builders) {
        runRouteBuilders(null, registry, builders)
    }

    static void runRouteBuilders(String start, Map<String, Object> registry, RouteBuilder... builders) {
        def simpleRegistry = new SimpleRegistry()
        simpleRegistry.putAll(registry)
        runRouteBuilders(start, simpleRegistry, builders)
    }

    static void runRouteBuilders(Registry registry, RouteBuilder... builders) {
        runRouteBuilders(null, registry, builders)
    }

    static void runRouteBuilders(String start, Registry registry, RouteBuilder... builders) {
        def camelContext = new DefaultCamelContext(registry)
        try {
            components.each {
                camelContext.addComponent(it.key, it.value.newInstance(camelContext))
            }
            builders.each {
                camelContext.addRoutes (it)
            }
            camelContext.start()
            if (start) {
                camelContext.createProducerTemplate().requestBody(start, ObjectUtils.NULL)
            } else {
                def callStart = false
                camelContext.routes.each {
                    def consumer = it.consumer
                    def uri = consumer.endpoint.endpointUri
                    if ("direct://start" == uri) {
                        callStart = true
                    }
                }
                if(callStart) {
                    camelContext.createProducerTemplate().requestBody("direct://start", ObjectUtils.NULL)
                }
            }
            CamelUtils.waitTillDone(camelContext)
            builders.each {
                if(it instanceof ManagedRouteBuilder) {
                    if (it.firstException) {
                        throw it.firstException
                    }
                }
            }
        } finally {
            try {
                camelContext.shutdown()
            } catch (Exception e) {
                log.warn("Unexpected exception occurred while shutting down camel", e)
            }
        }

    }
}
