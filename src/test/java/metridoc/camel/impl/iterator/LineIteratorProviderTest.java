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

package metridoc.camel.impl.iterator;

import metridoc.camel.test.RouteTest;
import org.apache.camel.EndpointInject;
import org.apache.camel.Expression;
import org.apache.camel.builder.ExpressionBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;

/**
 *
 * @author tbarker
 */
public class LineIteratorProviderTest extends RouteTest {

    @EndpointInject(uri="mock:end")
    private MockEndpoint mockEnd;

    @Test
    public void testLineIterator() throws InterruptedException {
        mockEnd.expectedMessageCount(4);
        mockEnd.assertIsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {

            @Override
            public void configure() throws Exception {
                Expression bean = ExpressionBuilder.beanExpression(LineIteratorCreator.class, "create");
                from("file:target/test-classes/testFiles/metridoc/camel/impl/iterator/polling?initialDelay=0&noop=true")
                        .split(bean).streaming().to("mock:end");
            }

        };
    }
}
