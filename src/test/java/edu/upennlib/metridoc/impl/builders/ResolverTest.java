/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.upennlib.metridoc.impl.builders;

import static edu.upennlib.metridoc.FluentCore.*;
import org.apache.camel.EndpointInject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

/**
 *
 * @author tbarker
 */
public class ResolverTest extends CamelTestSupport {

    @EndpointInject(uri="mock:resolverEnd")
    private MockEndpoint mockEnd;
    @EndpointInject(uri="mock:a")
    private MockEndpoint mockA;
    @EndpointInject(uri="mock:b")
    private MockEndpoint mockB;

    @Test
    public void testRoute() throws InterruptedException {
        mockEnd.expectsMessageCount(1);
        mockA.expectsMessageCount(1);
        mockB.expectsMessageCount(1);

        template.sendBody("direct:start", new Object());
        mockB.assertIsSatisfied();
        mockA.assertIsSatisfied();
        mockEnd.assertIsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {

            @Override
            public void configure() throws Exception {
                includeRoutes(resolvers("direct:a", "direct:b").startFrom("direct:resolver").endTo("mock:resolverEnd"));
                from("direct:start").to("direct:resolver");
                from("direct:a").to("mock:a");
                from("direct:b").to("mock:b");
            }
        };
    }
}
