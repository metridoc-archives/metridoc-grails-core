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
package metridoc.utils

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.test.junit4.CamelTestSupport
import org.junit.Test

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 8/26/11
 * Time: 4:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class CamelUtilsTest extends CamelTestSupport {

    static CountDownLatch sedaTestLatch = new CountDownLatch(3)
    static CountDownLatch inflightLatch = new CountDownLatch(1)
    static CountDownLatch aggLatch = new CountDownLatch(1)

    /**
     * by putting the tests together, we speed everything up by using a cached instance of camel
     */
    @Test
    void testAllScenarios() {
        testWaitingTillDoneOnSeda()
        testWaitingForInflight()
    }

    void testWaitingTillDoneOnSeda() {
        def items = ["foo", "bar", "baz"]
        template.sendBody("direct:sedaTest", items)
        assert sedaTestLatch.count > 0
        assert !CamelUtils.waitTillDone(context, 5, TimeUnit.MILLISECONDS)
        assert CamelUtils.waitTillDone(context, 5, TimeUnit.SECONDS)
        assert 0L == sedaTestLatch.count
    }

    void testWaitingForInflight() {
        template.sendBody("seda:inflight", ObjectUtils.NULL)
        assert !CamelUtils.waitTillDone(context, 1, TimeUnit.NANOSECONDS)
        assert CamelUtils.waitTillDone(context, 10, TimeUnit.SECONDS)
        assert 0L == inflightLatch.count
    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        return routeBuilder
    }

    RouteBuilder routeBuilder = new RouteBuilder() {
        @Override
        void configure() {

            from("direct:sedaTest").split(body()).to("seda:sedaTest")
            from("seda:sedaTest").process(
                new Processor() {
                    void process(Exchange exchange) {
                        Thread.sleep(500)
                        CamelUtilsTest.sedaTestLatch.countDown()
                    }
                }
            )

            from("seda:inflight?concurrentConsumers=5").process(
                new Processor() {
                    void process(Exchange exchange) {
                        Thread.sleep(500)
                        CamelUtilsTest.inflightLatch.countDown()
                    }
                }
            )
        }
    }
}
