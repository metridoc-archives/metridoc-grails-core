/**
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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.upennlib.metridoc.impl;

import edu.upennlib.metridoc.test.RouteTest;
import static edu.upennlib.metridoc.FluentCore.*;
import static edu.upennlib.metridoc.url.DefaultUris.*;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author tbarker
 */
public class DefaultMessageSenderTest extends RouteTest{

    @EndpointInject(uri="mock:a")
    private MockEndpoint mockA;
    @EndpointInject(uri="mock:b")
    private MockEndpoint mockB;
    private DefaultMessageSender defaultMessageSender;

    @Before
    public void setUpSender() {
        defaultMessageSender = new DefaultMessageSender();
        defaultMessageSender.setCamelContext(context);
        defaultMessageSender.setProducerTemplate(template);
    }

    @Test
    public void getProducerTemplateShouldNotBeNullEver() {
        assertNotNull(defaultMessageSender.getProducerTemplate());
    }

    @Test
    public void getProducerTemplateShouldBeTheSameEveryTime() {
        assertEquals(defaultMessageSender.getProducerTemplate(),
                defaultMessageSender.getProducerTemplate());
    }

//    @Test
//    public void messageShouldBeSentToExceptionEndpointWhenHandled() throws InterruptedException {
//        mockA.expectedMessageCount(1);
//        defaultMessageSender.handle(new NullPointerException("handle me!"));
//        mockA.assertIsSatisfied();
//    }
//
//    @Test
//    public void exceptionShouldBeAccessibleInExchangeViaProperties() throws InterruptedException {
//        mockA.expectedMessageCount(1);
//        mockA.whenAnyExchangeReceived(
//            new Processor(){
//
//                @Override
//                public void process(Exchange exchange) throws Exception {
//                    assertNotNull(exchange.getProperty(Exchange.EXCEPTION_CAUGHT));
//                }
//            }
//        );
//        defaultMessageSender.handle(new NullPointerException("handle me!"));
//        mockA.assertIsSatisfied();
//
//    }

    @Test
    public void channelShouldReceiveBodyAndHeader() throws InterruptedException {
        String body = "body";
        Map<String, Object> headers = new TreeMap<String, Object>();
        headers.put("key1", "header1");
        headers.put("key2", "header2");
        mockB.expectedMessageCount(1);
        defaultMessageSender.sendBodyAndHeadersToChannel(body, headers);
        mockB.assertIsSatisfied();
    }

    @Test
    public void channelShouldReceiveBodyAndHeaderAsynchronously() throws InterruptedException, ExecutionException {
        String body = "body";
        Map<String, Object> headers = new TreeMap<String, Object>();
        headers.put("key1", "header1");
        headers.put("key2", "header2");
        mockB.expectedMessageCount(1);
        Future future = defaultMessageSender.asyncSendBodyAndHeadersToChannel(body, headers);
        future.get();
        mockB.assertIsSatisfied();
    }



    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {

            @Override
            public void configure() throws Exception {
                from(url(EXCEPTION_HANDLER)).to("mock:a");
                from(url(METRIDOC_CHANNEL)).to("mock:b");
            }
        };
    }



}