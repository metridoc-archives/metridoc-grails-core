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

package metridoc.component.transform;

import java.util.Map;
import metridoc.component.transform.FooBar.Person;
import org.apache.camel.impl.DefaultExchange;
import metridoc.test.StandardTest;
import java.lang.reflect.Method;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.junit.Test;
import static org.easymock.EasyMock.*;

/**
 *
 * @author tbarker
 */
public class ReduceAggregationStrategyTest extends StandardTest{

    private Method method;
    private ReduceAggregationStrategy agg;
    private FooBar fooBar;
    
    @Test
    public void arrayFromReturnedExchangeHasAnArrayTheSizeOfMethodParams() throws Exception {
        ReduceAggregationStrategy aggStrat = agg();
        Exchange exchange = aggStrat.aggregate(null, exchange("hello", 0));
        Map params = exchange.getIn().getBody(Map.class);
        assertEquals(1, params.size());
    }
    
    @Test
    public void bodyOfFirstExchangeIsPlacedInAnArray() throws Exception {
        Exchange exchange = exchange(30, 1);
        exchange = agg().aggregate(null, exchange);
        Map params = exchange.getIn().getBody(Map.class);
        assertEquals(30, params.get(1));
    }

    @Test
    public void ifGivenAllExchangesReduceMethodIsInvoked() throws Exception {
        Exchange exchange;
        Exchange exchange1 = exchange(30, 1);
        Exchange exchange2 = exchange("fooBar", 0);
        exchange = agg().aggregate(null, exchange1);
        exchange = agg().aggregate(exchange, exchange2);

        Person person = exchange.getIn().getBody(Person.class);
        assertEquals(30, person.getAge());
        assertEquals("fooBar", person.getName());
    }
    
    private Method method() throws Exception {
        if (method == null) {
            method = FooBar.class.getMethod("reduce", String.class, int.class);
        }
        
        return method;
    }
    
    private FooBar fooBar() {
        if (fooBar == null) {
            fooBar = new FooBar();
        }
        
        return fooBar;
    }
    
    private ReduceAggregationStrategy agg() throws Exception{
        if (agg == null) {
            agg = new ReduceAggregationStrategy(fooBar(), method());
        }
        
        return agg;
    }
    
    private Exchange exchange(Object body, int order) {
        Exchange exchange = new DefaultExchange(createMock(CamelContext.class));
        exchange.getIn().setBody(body);
        exchange.getIn().setHeader(ReduceAggregationStrategy.PARAM_POSITION, order);
        
        return exchange;
    }
}