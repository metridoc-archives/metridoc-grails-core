/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.upennlib.metridoc.impl;

import edu.upennlib.metridoc.MessageSender;
import static edu.upennlib.metridoc.url.DefaultUris.*;
import static edu.upennlib.metridoc.FluentCore.*;
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
