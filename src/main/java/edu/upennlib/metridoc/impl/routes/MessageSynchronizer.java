/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.upennlib.metridoc.impl.routes;

import org.apache.camel.impl.DefaultExchange;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.lang.ObjectUtils;
import static edu.upennlib.metridoc.FluentCore.*;
import static edu.upennlib.metridoc.url.DefaultUris.*;

/**
 *
 * @author tbarker
 */
public class MessageSynchronizer {

    public static final String BEAN_NAME = "messageSynchronizer";
    private Map<String, ReentrantLock> locks = new HashMap<String, ReentrantLock>();
    private ReentrantLock helperLock = new ReentrantLock();
    private ProducerTemplate producerTemplate;

    public void synchronize(Exchange exchange) {

        Lock syncer = null;
        try {
//            helperLock.lock();
//            RouteConfig config = exchange.getIn().getHeader(RouteConfig.ROUTE_CONFIG, RouteConfig.class);
//            if (config != null) {
//                if (config.getSyncGroup() != null) {
//                    if (!locks.containsKey(config.getSyncGroup())) {
//                        locks.put(config.getSyncGroup(), new ReentrantLock(true));
//                    }
//                    syncer = locks.get(config.getSyncGroup());
//                }
//            }
        } finally {
            helperLock.unlock();
        }

        send(exchange, syncer);
    }

    private void send(Exchange exchange, Lock syncer) {
        try {
            if (syncer != null) {
                syncer.lock();
            }
            producerTemplate =
                    (ProducerTemplate) ObjectUtils.defaultIfNull(producerTemplate, exchange.getContext()
                    .createProducerTemplate());
            Exchange response = producerTemplate.send(url(METRIDOC_CHANNEL), exchange);
            if (response.getException() != null) {
                Exchange exceptionExchange = new DefaultExchange(exchange.getContext());
                exceptionExchange.setProperty(Exchange.EXCEPTION_CAUGHT, response.getException());
                producerTemplate.send(url(EXCEPTION_HANDLER), exceptionExchange);
            }
        } finally {
            if (syncer != null) {
                syncer.unlock();
            }
        }
    }

}
