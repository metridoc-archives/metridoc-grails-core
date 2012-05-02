/*
 * Copyright 2010 Trustees of the University of Pennsylvania Licensed under the
 * Educational Community License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package metridoc.plugins.camel

import metridoc.camel.builder.GroovyRouteBuilder
import metridoc.camel.context.MetridocCamelContext
import metridoc.camel.registry.MetridocSimpleRegistry
import metridoc.plugins.Plugin
import metridoc.utils.Assert
import metridoc.utils.CamelUtils
import metridoc.utils.ObjectUtils
import org.apache.camel.CamelContext
import org.apache.camel.Endpoint
import org.apache.camel.ProducerTemplate
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.spi.Registry
import org.apache.camel.util.ServiceHelper
import org.slf4j.LoggerFactory

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 7/3/11
 * Time: 9:05 PM
 *
 * Creates extensions to a job script to do camel routing
 *
 * @see CamelExtensionPlugin
 *
 */
@Plugin(category = "job")
class CamelPlugin {

    static final METRIDOC_CAMEL_CONTEXT = "metridocCamelContext"
    static final METRIDOC_CAMEL_REGISTRY = "metridocCamelRegistry"
    static final METRIDOC_STOP_CAMEL = "metridocTopCamel"
    static final camelLog = LoggerFactory.getLogger(CamelPlugin.class)

    /**
     * best effort to retrieve a service of a certain type from the property services.  The services property is a
     * mixed in property,see class level javadoc for details
     *
     * @param name name of the service
     * @param type type of the service to retrieve
     * @return the service if it exists, otherwise null
     */
    static <T> T getCamelService(Script self, String name, Class<T> type) {
        Binding services = self.binding
        Assert.notNull(services, "services in the CamelPlugin must be set to create or retrieve camel components")
        def bindingVariables = services.variables

        if (bindingVariables.containsKey(name)) {
            def possibleValue = services[name]

            if (type.isInstance(possibleValue)) {
                return possibleValue
            }
        }

        //didn't find it
        return null
    }

    static Registry getRegistry(Script self) {
        def services = self.binding
        def defaultService = {
            new MetridocSimpleRegistry(services)
        }

        getServiceAndSetProperty(self, "registry", METRIDOC_CAMEL_REGISTRY, Registry.class, defaultService)
    }

    static CamelContext getCamelContext(Script script) {

        def defaultService = {
            def camelContext = new MetridocCamelContext(getRegistry(script))
            return camelContext
        }

        getServiceAndSetProperty(script, "camelContext", METRIDOC_CAMEL_CONTEXT, CamelContext.class, defaultService)
    }

    static Endpoint getEndpoint(Script self, String uri) {
        getCamelContext(self).getEndpoint(uri)
    }

    /**
     * retrieves a service by name and type from services.  If it is not in services the
     * <code>getDefault</code> function is called to retrieve the default object.  If getDefault returns null
     * then an {@link IllegalArgumentException} is thrown.  The property is set to whatever value is found
     *
     * @param propertyName name of the property, it must exist or else a ${@link MissingPropertyException} is thrown
     * @param serviceName the name of the service in services
     * @param type type of the service
     * @param getDefault closure to call to retrieve the default, must not return null
     * @return the value that we are looking for
     */
    static <T> T getServiceAndSetProperty(Script self, String propertyName, String serviceName, Class<T> type, Closure getDefault) {
        def service = getCamelService(self, serviceName, type)
        if (service != null) return service

        service = getDefault()
        Assert.notNull(service, "When retrieving a service, the passed default function 'getDefault' must " +
            "not be null")
        def services = self.binding
        services[serviceName] = service //set the service

        return service
    }

    static RouteBuilder runRoute(Script self, LinkedHashMap args = [:], Closure closure) {

        def routeBuilder = new GroovyRouteBuilder()
        routeBuilder.setRoute(closure)

        if (args && args.containsKey("usePolling")) {
            routeBuilder.setUsePolling(args.usePolling)
        }

        CamelContext camelContext
        if (args.containsKey("camelContext")) {
            camelContext = args.camelContext
        } else {
            camelContext = getAndStartCamelContext(self)
        }

        camelContext.addRoutes(routeBuilder)
        CamelUtils.waitTillDone(camelContext)

        def exception = routeBuilder.routeException
        if (exception) {
            throw exception
        }

        return routeBuilder
    }

    static void runRoute(Script self, RouteBuilder routeBuilder) {
        def camelContext = getAndStartCamelContext(self)
        camelContext.addRoutes(routeBuilder)
    }

    private static CamelContext getAndStartCamelContext(Script self) {
        def camelContext = getCamelContext(self) //use get to force initialization if not set
        if (!camelContext.status.started) {
            camelContext.start()
        }

        return camelContext
    }

    /**
     * Calls a route and gets the result
     *
     * @param route the route to call
     * @param body the body, default to null
     * @param headers the headers as a map, defaults to an empty map
     * @return he result
     */
    static def runRoute(Script self, String route, body = ObjectUtils.NULL, Map headers = [:]) {
        def camelContext = getCamelContext(self)
        ProducerTemplate template = camelContext.createProducerTemplate()
        def answer = template.requestBodyAndHeaders(route, body, headers);
        ServiceHelper.stopService(template)
        return answer
    }

    static def runAsyncRoute(Script self, String route, body = ObjectUtils.NULL, Map headers = [:]) {
        def camelContext = getCamelContext(self)
        ProducerTemplate template = camelContext.createProducerTemplate()
        def answer = template.asyncRequestBodyAndHeaders(route, body, headers);

        return answer
    }

    private static void stopCamelContext(Script self, CamelContext camelContext) {
        if (camelContext.status.stopped) {
            camelLog.debug("camel has already stopped, no need to stop it")

        }

        def variables = self.binding.variables
        def stopCamel = variables[CamelPlugin.METRIDOC_STOP_CAMEL]
        if (stopCamel == null) {
            stopCamel = true
        }

        if (stopCamel) {
            CamelUtils.waitTillDone(camelContext)
        }

        if (stopCamel) {
            if (camelContext != null) {
                camelContext.stop()
            }
        }
    }
}

