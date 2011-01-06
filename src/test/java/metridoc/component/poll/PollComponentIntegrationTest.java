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

package metridoc.component.poll;

import metridoc.test.RouteTest;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;

/**
 *
 * @author Tommy Barker
 */
public class PollComponentIntegrationTest extends RouteTest{

    @EndpointInject(uri="mock:end")
    public MockEndpoint mockEnd;

    @Test
    public void testMessagePolling() throws Exception {
        mockEnd.expectedMessageCount(1);
        template.sendBody("direct:poll", "hello");
        template.sendBodyAndHeader("direct:start","goodBye", "foo", "bar");
        mockEnd.assertIsSatisfied();
    }

    @Test
    public void testThatHeadersFollowThroughFromPollingLocation() throws Exception {
        mockEnd.expectedMessageCount(1);

        mockEnd.setReporter(new Processor() {

            @Override
            public void process(Exchange exchange) throws Exception {
                assertNotNull(exchange.getIn().getHeader("foo"));
            }
        });

        template.sendBody("direct:poll", "hello");
        template.sendBodyAndHeader("direct:start","hello", "foo", "bar");
        mockEnd.assertIsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder(){

            @Override
            public void configure() throws Exception {
                from("direct:start").to("poll:direct:poll", "mock:end");
            }
        };
    }



}
