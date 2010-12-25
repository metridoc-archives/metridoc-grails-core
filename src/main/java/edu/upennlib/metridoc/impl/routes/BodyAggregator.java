/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.upennlib.metridoc.impl.routes;

import java.util.ArrayList;
import java.util.List;
import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

/**
 *
 * @author Thomas Barker
 */
public class BodyAggregator implements AggregationStrategy {

    @Override
    @SuppressWarnings("unchecked")
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        
        if (oldExchange == null) {
            oldExchange = newExchange.copy();
            List<Object> body = new ArrayList<Object>();
            oldExchange.getIn().setBody(body);
        }
        oldExchange.getIn().getBody(List.class).add(newExchange.getIn().getBody());
        
        return oldExchange;
    }

}
