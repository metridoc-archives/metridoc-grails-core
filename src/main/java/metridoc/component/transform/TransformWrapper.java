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

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.processor.MulticastProcessor;

/**
 *
 * @author tbarker
 */
public class TransformWrapper implements Processor{

    private MulticastProcessor multicastProcessor;
    
    public static TransformWrapper create(Object object) {
        return null;
    }
    
    @Override
    public void process(Exchange exchange) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
