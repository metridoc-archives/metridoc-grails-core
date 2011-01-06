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

import org.junit.Ignore;
import org.apache.camel.Processor;
import metridoc.component.transform.TransformProcessorEndpoint.ServiceNotFoundException;
import org.apache.camel.CamelContext;
import org.junit.Assert;
import org.junit.Test;
import static org.easymock.EasyMock.*;

/**
 *
 * @author tbarker
 */
public class MulticastProcessorEndpointTest extends Assert {

    private TransformProcessorEndpoint multicastProcessorEndpoint;
    
    @Test
    public void serviceNotFoundReturnedIfServiceDoesNotExistWhenGettingMulticastBean() {
        try {
            endpoint().getMulticastBean("foo");
            fail("exception should have occurred");
        } catch (ServiceNotFoundException e) {
            
        }
    }

    @Ignore
    @Test
    public void classPathMulticastBeansCanBeFoundAndInstantiated() {
        Object object = endpoint().getMulticastBean("fooBar");
        assertTrue(object instanceof FooBar);
    }
    
    @Test
    public void serviceNotFoundExceptionReturnsMessageBasedOnServiceName() {
        
        try {
            throw new ServiceNotFoundException("foo");
        } catch (ServiceNotFoundException e) {
            assertEquals(
                String.format(ServiceNotFoundException.SERVICE_NOT_FOUND, "foo"),
                e.getMessage()
            );
        }
    }
    
    private TransformProcessorEndpoint endpoint() {
        if (multicastProcessorEndpoint == null) {
            multicastProcessorEndpoint = new TransformProcessorEndpoint("foo://bar",
                    createMock(CamelContext.class),
                    createMock(Processor.class));
        }
        
        return multicastProcessorEndpoint;
    }

}