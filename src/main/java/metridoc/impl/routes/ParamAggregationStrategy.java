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

package metridoc.impl.routes;

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
