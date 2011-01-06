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

import java.lang.reflect.Method;
import java.util.Map;
import java.util.TreeMap;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

/**
 *
 * @author tbarker
 */
public class ReduceAggregationStrategy implements AggregationStrategy{
    
    private Method method;
    private Object methodInvoker;
    public static final String PARAM_POSITION = "Metridoc.Param.Position";
    
    public ReduceAggregationStrategy(Object object, Method method) {
        this.method = method;
        this.methodInvoker = object;
    }
    
    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        Map<Integer, Object> paramMap;
        if (oldExchange == null) {
            oldExchange = new DefaultExchange(newExchange);
            paramMap = new TreeMap<Integer, Object>();
            oldExchange.getIn().setBody(paramMap);
        }
        
        paramMap = oldExchange.getIn().getBody(Map.class);
        int paramPosition = newExchange.getIn().getHeader(PARAM_POSITION, Integer.class);
        paramMap.put(paramPosition, newExchange.getIn().getBody());

        if (paramMap.size() == method.getParameterTypes().length) {
            Object[] params = paramMap.values().toArray();
            try {
                Object body = method.invoke(methodInvoker, params);
                oldExchange.getIn().setBody(body);
            } catch (Exception ex) {
                //TODO: let's make this better, ie throw an appropriate exception and give a better message
                throw new RuntimeException("could not reduce with passed parameters", ex);
            }
        }

        return oldExchange;
    }

}
