/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.upennlib.metridoc.impl.routes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

/**
 *
 * @author tbarker
 */
public class ParamAggregationStrategy implements AggregationStrategy{

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        if (oldExchange == null) {
            oldExchange = newExchange.copy();
            oldExchange.getIn().setBody(new ArrayList());
        }
        
        Object[] params = newExchange.getIn().getBody(Object[].class);
        List currentList = oldExchange.getIn().getBody(List.class);
        currentList.addAll(Arrays.asList(params));
        
        return oldExchange;
    }

}
