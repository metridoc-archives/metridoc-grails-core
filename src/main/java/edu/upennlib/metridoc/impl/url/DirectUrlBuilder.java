/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.upennlib.metridoc.impl.url;

import edu.upennlib.metridoc.url.UrlBuilder;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.Validate;

/**
 *
 * @author tbarker
 */
public class DirectUrlBuilder implements UrlBuilder{

    private String name;
    private String id;
    
    public DirectUrlBuilder name(String name) {
        Validate.notEmpty(name);
        this.name = name;
        return this;
    }
    
    public DirectUrlBuilder id(String id) {
        Validate.notEmpty(id);
        this.id = id;
        return this;
    }
    
    public DirectUrlBuilder randomId() {
        this.id = RandomStringUtils.randomAlphanumeric(6);
        return this;
    }
    
    @Override
    public String url() {
        Validate.notEmpty(name);
        String result = "direct:" + name;
        if (id != null) {
            result += "-" + id;
        }
        return result;
    }
    
}
