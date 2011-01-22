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

package metridoc.component.poll;

import metridoc.test.StandardTest;
import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.PollingConsumer;
import org.apache.camel.processor.PollEnricher;
import org.junit.Test;
import static org.easymock.EasyMock.*;

/**
 *
 * @author tbarker
 */
public class PollProducerTest extends StandardTest{

    private CamelContext camelContext;
    private Endpoint endpoint;
    private Endpoint pollingEndpoint;
    private PollingConsumer pollingConsumer;
    private PollProducer pollProducer;
    private PollEnricher pollEnricher;

    @Test
    public void extractPollingUrlWillReturnTheUrlOfPollingConsumer() {
        String url = "poll:fooBar:someUrl?bar=baz";
        url = PollProducer.extractPollingUri(url);
        assertEquals("fooBar:someUrl?bar=baz", url);
        url = "poll://fooBar:someUrl?bar=baz";
        url = PollProducer.extractPollingUri(url);
        assertEquals("fooBar:someUrl?bar=baz", url);
    }

    @Test
    public void gettingPollEnricherIsNeverNull() throws Exception {
        replay(endpoint(), camelContext(), pollingConsumer(), pollingEndpoint());
        assertNotNull(pollProducer().getPollEnricher());
        verify(endpoint(), camelContext(), pollingConsumer(), pollingEndpoint());
    }

    @Test
    public void callingProcessCallsPollEnricherProcess() throws Exception {
        replay(pollEnricher(), endpoint(), camelContext(), pollingConsumer(), pollingEndpoint());
        pollProducer().setPollEnricher(pollEnricher());
        pollProducer().process(createMock(Exchange.class));
        verify(pollEnricher());
    }

    private PollingConsumer pollingConsumer() {
        if (pollingConsumer == null) {
            pollingConsumer = createMock(PollingConsumer.class);
        }

        return pollingConsumer;
    }

    private Endpoint pollingEndpoint() throws Exception {
        if (pollingEndpoint == null) {
            pollingEndpoint = createMock(Endpoint.class);
            expect(pollingEndpoint.createPollingConsumer()).andReturn(pollingConsumer()).atLeastOnce();
        }

        return pollingEndpoint;
    }

    private Endpoint endpoint() throws Exception {
        if (endpoint == null) {
            endpoint = createMock(Endpoint.class);
            expect(endpoint.getEndpointUri()).andReturn("poll://fooBar:someUrl?bar=baz").atLeastOnce();
            expect(endpoint.getCamelContext()).andReturn(camelContext()).atLeastOnce();
        }

        return endpoint;
    }

    private CamelContext camelContext() throws Exception {
        if (camelContext == null) {
            camelContext = createMock(CamelContext.class);
            expect(camelContext.getEndpoint("fooBar:someUrl?bar=baz")).andReturn(pollingEndpoint()).atLeastOnce();
        }

        return camelContext;
    }

    private PollProducer pollProducer() throws Exception {
        if (pollProducer == null) {
            pollProducer = new PollProducer(endpoint());
        }

        return pollProducer;
    }

    private PollEnricher pollEnricher() throws Exception {
        if (pollEnricher == null) {
            pollEnricher = createMock(PollEnricher.class);
            pollEnricher.process(anyObject(Exchange.class));
        }

        return pollEnricher;
    }

}