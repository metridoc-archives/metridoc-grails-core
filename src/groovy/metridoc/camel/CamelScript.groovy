package metridoc.camel

import metridoc.utils.CamelUtils
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
class CamelScript {
    static RouteBuilder runRoute(Closure closure) {
        runRoute(null, closure)
    }

    static RouteBuilder runRoute(String start, Closure closure) {
        def routeBuilder = new GroovyRouteBuilder(route: closure)
        def registry = new CamelScriptRegistry(closure: closure)
        runRouteBuilders(start, registry, routeBuilder)
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
        builders.each {
            camelContext.addRoutes (it)
        }
        camelContext.start()
        if (start) {
            camelContext.createProducerTemplate().requestBody(start, ObjectUtils.NULL)
        }
        CamelUtils.waitTillDone(camelContext)
        camelContext.shutdown()
    }
}
