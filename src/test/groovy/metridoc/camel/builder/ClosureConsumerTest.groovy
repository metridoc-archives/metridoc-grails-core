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

import org.junit.Test
import org.apache.camel.Processor
import org.apache.camel.Exchange
import org.apache.camel.CamelContext
import org.apache.camel.Endpoint

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 1/4/12
 * Time: 2:36 PM
 */
class ClosureConsumerTest {

    @Test
    void testBasicConsumption() {

        def processorCalled = false
        def processor = [
                process: {Exchange exchange ->
                    assert "hey" == exchange.in.body
                    processorCalled = true
                }
        ] as Processor

        def consumer = new ClosureConsumer(consumerClosure: {"hey"}, processor: processor, endpoint: endpoint())
        consumer.start()
        assert processorCalled
    }

    @Test
    void testIteratedConsumption() {
        testIteratedConsumption(true)
        testIteratedConsumption(false)
    }

    void testIteratedConsumption(boolean doIterate) {
        def processorCalled = false
        def timesCalled = 0
        def processor = [
                process: {Exchange exchange ->
                    timesCalled++
                    processorCalled = true
                }
        ] as Processor

        def consumer = new ClosureConsumer(consumerClosure: {iterate = doIterate; [1,2,3]}, processor: processor, endpoint: endpoint())
        consumer.start()
        assert processorCalled
        if(doIterate) {
            assert 3 == timesCalled
        } else {
            assert 1 == timesCalled
        }
    }

    Endpoint endpoint() {
        [
            getCamelContext: {
                [] as CamelContext
            }
        ] as Endpoint
    }
}
