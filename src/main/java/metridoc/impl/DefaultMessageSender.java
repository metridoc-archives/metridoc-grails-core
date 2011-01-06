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

package metridoc.impl;

import metridoc.MessageSender;
import static metridoc.url.DefaultUris.*;
import static metridoc.FluentCore.*;
import java.util.Map;
import java.util.concurrent.Future;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultExchange;

/**
 *
 * @author tbarker
 */
public class DefaultMessageSender implements MessageSender{
    private CamelContext camelContext;
    private ProducerTemplate producerTemplate;

    public CamelContext getCamelContext() {
        return camelContext;
    }

    public void setCamelContext(CamelContext camelContext) {
        this.camelContext = camelContext;
    }

    @Override
    public void sendBodyAndHeadersToChannel(Object body, Map<String, Object> headers) {
        getProducerTemplate().sendBodyAndHeaders(url(METRIDOC_CHANNEL), body, headers);
    }

    public ProducerTemplate getProducerTemplate() {
        return producerTemplate;
    }

    public void setProducerTemplate(ProducerTemplate producerTemplate) {
        this.producerTemplate = producerTemplate;
    }

    @Override
    public Future asyncSendBodyAndHeadersToChannel(Object body, Map<String, Object> headers) {
        return getProducerTemplate().asyncRequestBodyAndHeaders(url(METRIDOC_CHANNEL), body, headers);
    }

}
