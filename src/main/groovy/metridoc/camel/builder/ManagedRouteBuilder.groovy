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
package metridoc.camel.builder

import metridoc.plugins.PluginDB
import org.apache.camel.CamelContext
import org.apache.camel.Exchange
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.model.RouteDefinition
import org.apache.camel.spi.BrowsableEndpoint

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 8/26/11
 * Time: 2:02 PM
 * To change this template use File | Settings | File Templates.
 */
abstract class ManagedRouteBuilder extends RouteBuilder implements ExceptionHandler {

    Throwable routeException
    Boolean usePolling
    Closure exceptionHandler

    @Override
    RouteDefinition from(String uri) {
        def newUri = uri

        if (usePolling(uri)) {
            newUri = "poll:${newUri}"
        }

        super.from newUri
    }

    @Override
    void configure() {
        def camelPlugins = PluginDB.getInstance().getPlugins("camel")
        log.debug("using plugins {} in the route", camelPlugins)
        use(camelPlugins) {

            onException(Throwable.class).process {Exchange exchange ->
                def exception = exchange.getException()
                if (exception) {
                    handleException(exception, exchange)
                } else {
                    exception = exchange.getProperty(Exchange.EXCEPTION_CAUGHT)
                    if (exception) {
                        handleException(exception, exchange)
                    } else {
                        exception = new RuntimeException("An unknown exception occurred, if the log is not " +
                            "sufficient to determine what happened consider setting logging level to DEBUG")
                        handleException(exception, exchange)
                    }
                }
            }

            doConfigure()
        }
    }

    Throwable getFirstException() {
        return routeException
    }



    abstract void doConfigure()

    /**
     *
     * @param camelContext {@link CamelContext that ran this route
     */
    void waitTillDone() {
        def routeCollection = this.routeCollection
        def routes = routeCollection.routes
        def camelContext = routeCollection.camelContext
        def inflight = camelContext.inflightRepository
        def boolean activityDetected = true

        //best effort to continue until everything is finished
        while (activityDetected) {
            if (routes.size() == 0) {
                activityDetected = false
            }

            routes.each {route ->
                def fromDefinitions = route.inputs
                fromDefinitions.each {from ->
                    def uri = from.uri
                    def endpoint = camelContext.getEndpoint(uri)
                    def size = inflight.size(endpoint)

                    if (size == 0) {
                        activityDetected = false
                    }

                    if (endpoint instanceof BrowsableEndpoint && !activityDetected) {
                        size = endpoint.exchanges.size()
                        if (size == 0) {
                            activityDetected = false
                        }
                    }
                }
            }

            if (activityDetected) {
                Thread.sleep(100)
            }
        }

    }

    protected boolean usePolling(String uri) {

        def m = uri =~ /usePolling=(true|false)/
        def boolean usePollingInUri

        //uri based
        if (m.find()) {
            return Boolean.valueOf(m[0][1])
        }

        //global
        if (usePolling != null) {
            return usePolling
        }

        //defaults
        return !(uri.startsWith("seda") || uri.startsWith("direct"))
    }

    @Override
    void handleException(Throwable throwable, Exchange exchange) {
        if (exceptionHandler) {
            exceptionHandler.call(throwable, exchange)
        } else {

            if (routeException) {
                log.error("an additional exception occurred", throwable)
                return //only catch and throw the first one
            }

            routeException = throwable
        }
    }

}
