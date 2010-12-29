/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.upennlib.metridoc.component;

import java.util.Map;
import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;

/**
 *
 * @author tbarker
 */
public class MulticastComponent extends DefaultComponent{

    @Override
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        return new MulticastProcessorEndpoint();
    }
    
    

}
