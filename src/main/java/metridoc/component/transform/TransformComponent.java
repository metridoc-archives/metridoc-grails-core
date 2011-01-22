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
import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;
import org.apache.camel.impl.ProcessorEndpoint;
import org.apache.camel.processor.MulticastProcessor;

/**
 *
 * @author tbarker
 */
public class TransformComponent extends DefaultComponent{

    private boolean parallelProcessing = true;
    private String executorService = "defaultExecutorService";
    private int timeOut = 0;
    private boolean stopOnTimeOut = true;
    private boolean streaming = false;
    private String aggregationStrategy;

    @Override
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {

        MulticastProcessor processor =
                new MulticastProcessor(getCamelContext(), null, null, parallelProcessing, null, parallelProcessing,
                parallelProcessing, 0);
        
        return new ProcessorEndpoint(uri, null);
    }


    
    

}
