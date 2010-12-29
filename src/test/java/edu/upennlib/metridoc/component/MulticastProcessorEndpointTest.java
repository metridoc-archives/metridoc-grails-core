/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.upennlib.metridoc.component;

import edu.upennlib.metridoc.component.MulticastProcessorEndpoint.ServiceNotFoundException;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author tbarker
 */
public class MulticastProcessorEndpointTest extends Assert {

    private MulticastProcessorEndpoint multicastProcessorEndpoint;
    
    @Test
    public void serviceNotFoundReturnedIfServiceDoesNotExistWhenGettingMulticastBean() {
        try {
            endpoint().getMulticastBean("foo");
            fail("exception should have occurred");
        } catch (ServiceNotFoundException e) {
            
        }
    }
    
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
    
    private MulticastProcessorEndpoint endpoint() {
        if (multicastProcessorEndpoint == null) {
            multicastProcessorEndpoint = new MulticastProcessorEndpoint();
        }
        
        return multicastProcessorEndpoint;
    }

}