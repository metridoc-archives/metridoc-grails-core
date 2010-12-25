/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.upennlib.metridoc.impl.url;

import edu.upennlib.metridoc.url.UrlBuilder;
import org.apache.commons.lang.Validate;

/**
 *
 * @author Thomas Barker
 */
public class BeanUrlBuilder implements UrlBuilder {

    private String name;
    private String method;
    
    public BeanUrlBuilder name(String name) {
        this.name = name;
        return this;
    }
    
    public BeanUrlBuilder method(String method) {
        this.method = method;
        return this;
    }

    public String getMethod() {
        return method;
    }

    public String getName() {
        return name;
    }
    
    @Override
    public String url() {
        
        Validate.notEmpty(name, "service name has not been specified");
        
        String beanUrl = "bean:" + name;
        
        if (method != null) {
            beanUrl += "?method=" + method;
            
        }
        
        return beanUrl;
    }
    
}
