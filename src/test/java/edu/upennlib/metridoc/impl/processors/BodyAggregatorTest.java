/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.upennlib.metridoc.impl.processors;

import edu.upennlib.metridoc.impl.routes.BodyAggregator;
import java.util.List;
import java.util.ArrayList;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultExchange;
import org.junit.Test;
import org.apache.camel.test.junit4.CamelTestSupport;

/**
 *
 * @author Thomas Barker
 */
public class BodyAggregatorTest extends CamelTestSupport{

    
    @Test
    public void testAggregate() {
        DefaultExchange exchange = new DefaultExchange(context);
        exchange.getIn().setBody("line1");
        BodyAggregator aggregate = new BodyAggregator();
        Exchange aggExchange  = aggregate.aggregate(null, exchange);
        assertEquals(ArrayList.class, aggExchange.getIn().getBody().getClass());
        assertEquals(1, aggExchange.getIn().getBody(List.class).size());
        
        exchange = new DefaultExchange(context);
        exchange.getIn().setBody("line2");
        aggExchange  = aggregate.aggregate(aggExchange, exchange);
        assertEquals(2, aggExchange.getIn().getBody(List.class).size());
    }
    
    

}