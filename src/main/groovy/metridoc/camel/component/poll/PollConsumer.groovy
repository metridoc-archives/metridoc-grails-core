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
package metridoc.camel.component.poll

import groovy.util.logging.Slf4j
import metridoc.utils.Assert
import metridoc.utils.CollectionUtils.Maps
import metridoc.utils.StringUtils
import org.apache.camel.component.file.GenericFileEndpoint
import org.apache.camel.impl.ScheduledPollConsumer
import org.apache.camel.support.ServiceSupport
import org.apache.camel.*

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 4/22/11
 * Time: 10:26 AM
 * To change this template use File | Settings | File Templates.
 */
@Slf4j
class PollConsumer extends ServiceSupport implements Consumer, SuspendableService {

    def metridoc.camel.component.poll.PollEndpoint endpoint
    def Processor processor
    def static final propertiesToRemove = ["maxMessages", "wait", "usePolling"]

    def Endpoint getEndpoint() {
        return endpoint
    }

    @Override
    protected void doStart() {
        def postFix = "cannot be null in PollConsumer when doStart() is called"

        Assert.notNull processor, "the processor ${postFix}"
        Assert.notNull endpoint, "the endpoint ${postFix}"

        processMessages()
    }

    private void processMessages() {

        def endpointToConsume
        def formattedUri
        if (endpoint.wrappedEndpoint == null) {
            def uri = getEndpoint().getEndpointUri()
            formattedUri = formatUri(uri)
            endpointToConsume = resolveEndpoint(formattedUri)
        } else {
            endpointToConsume = endpoint.wrappedEndpoint
        }

        def exception
        def delegationProcessor = [
            process: {Exchange exchange ->
                if (!exception) {
                    getProcessor().process(exchange)
                    if (exchange.exception) {
                        exception = exchange.exception
                    }
                } else {
                    log.warn("because of a previous exception, ${exchange} cannot be processed")
                }
            }
        ] as Processor

        def consumer = endpointToConsume.createConsumer(delegationProcessor)
        if (consumer instanceof ScheduledPollConsumer) {
            consumer.run()
        } else {
            consumer.start()
        }

        if (exception) {
            throw exception
        }
    }

    @Override
    protected void doStop() {
        //Do nothing
    }

    def ConsumerTemplate getConsumerTemplate() {

        Assert.notNull endpoint, "the endpoint cannot be null"

        return endpoint.camelContext.createConsumerTemplate()
    }

    private static String formatUri(String uri) {
        def newUri = removePollPrefix(uri)

        propertiesToRemove.each {String name ->
            newUri = removePropertyFromUrl(newUri, name)
        }

        return newUri
    }

    private static String removePollPrefix(String url) {
        def newUrl = url.replace("poll://", StringUtils.EMPTY)

        if (url == newUrl) {
            newUrl = newUrl.replace("poll:", StringUtils.EMPTY)
        }

        return newUrl
    }

    private Endpoint resolveEndpoint(String uri) {
        def camelContext = getEndpoint().getCamelContext()
        def resolvedEndpoint = camelContext.getEndpoint(uri)

        if (resolvedEndpoint instanceof GenericFileEndpoint) {

            resolvedEndpoint.consumerProperties = Maps.createHashMap()
            resolvedEndpoint.consumerProperties["initialDelay"] = 0
            resolvedEndpoint.maxMessagesPerPoll = endpoint.maxMessages
            resolvedEndpoint.consumerProperties["delay"] = Integer.MAX_VALUE
        }

        return resolvedEndpoint
    }

    /**
     *
     * @param url the url to adjust
     * @param propertyName the property to remove
     * @return the new url with the property removed
     */
    private static String removePropertyFromUrl(String url, String propertyName) {
        def m = url =~ /(${propertyName}=[^&]+)/
        def String newUrl = url


        if (m.find()) {
            def property = m[0][1]
            newUrl = url.replace(property, StringUtils.EMPTY)
            newUrl = newUrl.replace("?&", "?")
            newUrl = newUrl.replace("&&", "&")
            newUrl = newUrl.replaceFirst("&\$", StringUtils.EMPTY)
            newUrl = newUrl.replaceFirst("\\?\$", StringUtils.EMPTY)
        }

        return newUrl
    }
}

