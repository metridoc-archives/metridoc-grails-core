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

import java.util.concurrent.CountDownLatch
import org.apache.camel.Consumer
import org.apache.camel.Endpoint
import org.apache.camel.impl.DefaultPollingConsumerPollStrategy
import org.slf4j.LoggerFactory

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 5/23/11
 * Time: 4:50 PM
 */
class ManagedPollStrategy extends DefaultPollingConsumerPollStrategy{
    def CountDownLatch latch
    def log = LoggerFactory.getLogger(ManagedPollStrategy.class)

    @Override
    void commit(Consumer consumer, Endpoint endpoint, int polledMessages) {
        def uri = endpoint.getEndpointUri()
        log.debug("{} file(s) have been polled and committed for uri {}", polledMessages, uri)
        super.commit(consumer, endpoint, polledMessages)
        countDown()
    }

    @Override
    boolean rollback(Consumer consumer, Endpoint endpoint, int retryCounter, Exception e) {
        countDown()
        return super.rollback(consumer, endpoint, retryCounter, e)
    }

    private synchronized countDown() {
        if(latch.count) {
            latch.countDown()
        }
    }


}
