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

import org.apache.camel.CamelContext;
import org.apache.camel.Processor;
import org.apache.camel.impl.ProcessorEndpoint;

/**
 *
 * @author tbarker
 */
public class TransformProcessorEndpoint extends ProcessorEndpoint {

    

    public TransformProcessorEndpoint(String endpointUri, CamelContext context, Processor processor) {
        super(endpointUri, context, processor);
    }
    
    
    
    /**
     * returns the service bean that will handle the multicast.  The search requires a multicast service to be
     * in the registry.  If you want to add multicasting options, use {@link MulticastService} to add these options
     * 
     * @param serviceName the name of the multicast service to search for
     * @return a multicast service
     * 
     * 
     */
    protected Object getMulticastBean(String serviceName) {
        
        throw new ServiceNotFoundException(serviceName);
    }
    
    public static class MultipleMulticastBeanException extends RuntimeException {

        public MultipleMulticastBeanException(String serviceName, Class... classes) {
            
        }

        @Override
        public String getMessage() {
            return super.getMessage();
        }
    }
    
    public static class ServiceNotFoundException extends RuntimeException {
        
        public static final String SERVICE_NOT_FOUND = "service %s could not be found";
        
        public ServiceNotFoundException(String serviceName) {
            super(createMessage(serviceName));
        }
        
        public static String createMessage(String serviceName) {
            return String.format(SERVICE_NOT_FOUND, serviceName);
        }
    }
    
}
