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
import org.apache.camel.Consumer
import org.apache.camel.Endpoint
import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.impl.ScheduledPollConsumer
import org.apache.camel.impl.ScheduledPollEndpoint
import org.apache.camel.model.RouteDefinition
import org.apache.camel.impl.DefaultConsumer

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
        def formattedUri = uri.replaceAll("maxMessages=", "maxMessagesPerPoll=")
        def endpoint = getContext().getEndpoint(formattedUri)

        if (endpoint instanceof ScheduledPollEndpoint) {
            def wrappedEndpoint = new ScheduledPollEndpointWrapper(scheduledPollEndpoint: endpoint, routeBuilder: this)
            return from(wrappedEndpoint as Endpoint)
        }

        super.from(formattedUri)
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

class ScheduledPollEndpointWrapper {
    ManagedRouteBuilder routeBuilder

    @Delegate
    ScheduledPollEndpoint scheduledPollEndpoint

    Consumer createConsumer(Processor processor) throws Exception {

        def delegationProcessor = [
            process: {Exchange exchange ->
                processor.process(exchange)

                def exception = exchange.exception
                if(exception) {
                    routeBuilder.handleException(exception, exchange)
                }
            }
        ] as Processor

        def consumerToWrap = scheduledPollEndpoint.createConsumer(delegationProcessor) as ScheduledPollConsumer
        return new ScheduledPollConsumerWrapper(scheduledPollEndpoint, processor, consumerToWrap)
    }
}

class ScheduledPollConsumerWrapper extends DefaultConsumer{

    ScheduledPollConsumer wrappedConsumer

    ScheduledPollConsumerWrapper(Endpoint endpoint, Processor processor, Consumer wrappedConsumer) {
        super(endpoint, processor)
        this.wrappedConsumer = wrappedConsumer
    }

    void doStart() throws Exception {
        wrappedConsumer.run()
    }
}