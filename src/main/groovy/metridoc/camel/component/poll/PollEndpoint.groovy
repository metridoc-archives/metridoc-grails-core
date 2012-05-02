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

import metridoc.utils.Assert
import org.apache.camel.Consumer
import org.apache.camel.Endpoint
import org.apache.camel.Processor
import org.apache.camel.Producer
import org.apache.camel.impl.DefaultEndpoint

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 4/24/11
 * Time: 3:15 PM
 * To change this template use File | Settings | File Templates.
 */
class PollEndpoint extends DefaultEndpoint {
    Endpoint wrappedEndpoint

    /**
     * the max messages that should be polled.  If it is 0 or less it will poll until it doesn't get anything
     */
    def int maxMessages = 1
    /**
     * The wait time in milliseconds for a poll to complete.  If null, the {@link metridoc.camel.component.poll.PollConsumer} will wait indefinitely.
     */
    def Long wait = 0

    Producer createProducer() {
        throw new UnsupportedOperationException("createProducer is not supported for the PollingEndpoint")
    }

    Consumer createConsumer(Processor processor) {
        Assert.notNull getCamelContext(), "the CamelContext has not been set in the PollEndpoint, cannot create the " +
            "consumer"
        return new metridoc.camel.component.poll.PollConsumer(endpoint: this, processor: processor)
    }

    /**
     * since we are essentially wrapping other endpoints, we want to make sure that properties specific to the wrapped
     * endpoint do not produce an error when they are not used by the PollingEndpoint
     *
     * @return true
     */
    @Override
    boolean isLenientProperties() {
        return true;
    }

    boolean isSingleton() {
        return true
    }

    @Override
    protected String createEndpointUri() {

        return wrappedEndpoint.getEndpointUri()

//        def result = "poll:${wrappedEndpoint.getEndpointUri()}"
//        def postFix = "maxMessages=${maxMessages}&wait=${wait}"
//
//        result.endsWith("?") ? "${result}${postFix}" : "${result}?${postFix}"
    }
}
