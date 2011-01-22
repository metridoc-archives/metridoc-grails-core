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

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.PollingConsumer;
import org.apache.camel.impl.DefaultProducer;
import org.apache.camel.processor.PollEnricher;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.camel.util.ExchangeHelper;

/**
 *
 * @author tbarker
 */
public class PollProducer extends DefaultProducer {

    private PollEnricher pollEnricher;
    private PollingConsumer pollingConsumer;
    private static final Logger LOGGER = LoggerFactory.getLogger(PollProducer.class);

    public PollProducer(Endpoint endpoint) throws Exception {
        super(endpoint);
        String uri = endpoint.getEndpointUri();
        String pollingUri = extractPollingUri(uri);
        Endpoint pollingEndpoint = getEndpoint().getCamelContext().getEndpoint(pollingUri);
        
        pollingConsumer = pollingEndpoint.createPollingConsumer();
        pollEnricher = new PollEnricher(new CopyAggregationStrategy(), pollingConsumer, 0);
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();
        pollingConsumer.start();
    }

    @Override
    protected void doStop() throws Exception {
        super.doStop();
        pollingConsumer.stop();
    }


    protected PollEnricher getPollEnricher() throws Exception {
        return pollEnricher;
    }

    protected void setPollEnricher(PollEnricher pollEnricher) {
        this.pollEnricher = pollEnricher;
    }

    protected static String extractPollingUri(String fullUrl) {
        Matcher m = PollEndpoint.VALID_PATTERN.matcher(fullUrl);
        m.matches();
        return m.group(1);
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        getPollEnricher().process(exchange);
    }

    public static class CopyAggregationStrategy implements AggregationStrategy {

        @Override
        public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
            if (newExchange != null) {
                Map<String, Object> headers = new HashMap(oldExchange.getIn().getHeaders());
                ExchangeHelper.copyResultsPreservePattern(oldExchange, newExchange);
                oldExchange.getIn().getHeaders().putAll(headers);
            }
            return oldExchange;
        }

    }

}
