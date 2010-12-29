/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.upennlib.metridoc.component;

import lombok.Getter;
import lombok.Setter;
import org.apache.camel.CamelContext;
import org.apache.camel.impl.ProcessorEndpoint;

/**
 *
 * @author tbarker
 */
public class MulticastProcessorEndpoint extends ProcessorEndpoint {

    @Getter @Setter private CamelContext camelContext;
    
    /**
     * returns the service bean that will handle the multicast.  The search is handled by first
     * checking if the endpoint already is managign an instance with the serviceName, next the 
     * registry is searched, and finally 
     * the classpath is searched for classes annotated with {@link MulticastBean} and returns
     * a bean that has the same class name as the beanName.  The name search ignores case, so a bean 
     * name of foobar could return a bean that has a class name of FooBar.  
     * 
     * @param beanName the name of the bean to search for
     * @return a multicast bean
     * 
     * 
     */
    protected Object getMulticastBean(String beanName) {
        
        throw new ServiceNotFoundException(beanName);
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
