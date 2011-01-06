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

package metridoc.impl.processors;

import metridoc.impl.routes.BodyAggregator;
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