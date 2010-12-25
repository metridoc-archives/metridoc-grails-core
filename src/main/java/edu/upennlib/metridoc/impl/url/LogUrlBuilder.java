/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.upennlib.metridoc.impl.url;

import edu.upennlib.metridoc.url.UrlBuilder;
import edu.upennlib.metridoc.url.UrlHelper;
import edu.upennlib.metridoc.url.UrlParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author tbarker
 */
public class LogUrlBuilder implements UrlBuilder{

    public enum Level{INFO, DEBUG, TRACE, ERROR, WARN}
    @UrlParam(include=false)
    private String name;
    private Level level;

    public LogUrlBuilder name(String name) {
        this.name = name;
        return this;
    }

    public LogUrlBuilder level(Level level) {
        this.level = level;
        return this;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public String url() {
        return UrlHelper.buildUrl("log", name, this);
    }


}
