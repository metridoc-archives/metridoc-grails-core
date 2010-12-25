/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.upennlib.metridoc.impl.url;

import edu.upennlib.metridoc.url.UrlBuilder;

/**
 *
 * @author Thomas Barker
 */
public class Mock implements UrlBuilder{
    
    private String name;
    
    public Mock name(String name) {
        this.name = name;
        return this;
    }

    @Override
    public String url() {
        return "mock:" + name;
    }
    
}
