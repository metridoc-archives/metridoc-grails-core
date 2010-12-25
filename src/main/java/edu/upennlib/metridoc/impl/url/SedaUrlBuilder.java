/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.upennlib.metridoc.impl.url;

import edu.upennlib.metridoc.url.UrlBuilder;
import edu.upennlib.metridoc.url.UrlHelper;
import edu.upennlib.metridoc.url.UrlParam;

/**
 *
 * @author tbarker
 */
public class SedaUrlBuilder implements UrlBuilder{

    
    public enum WaitForTaskToComplete{Always, Never, IfReplyExpected}
    
    private Integer size;
    private Integer concurrentConsumers;
    private WaitForTaskToComplete waitForTaskToComplete;
    private Long timeout;
    private Boolean multipleConsumers;
    private Boolean limitConcurrentConsumers;
    @UrlParam(include=false)
    private String name;
    @UrlParam(include=false)
    private String id;

    public SedaUrlBuilder id(String id) {
        this.id=id;
        return this;
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public SedaUrlBuilder size(int size) {
        this.size = size;
        return this;
    }
    
    public SedaUrlBuilder concurrentConsumers(int concurrentConsumers) {
        this.concurrentConsumers = concurrentConsumers;
        return this;
    }
    
    public SedaUrlBuilder waitForTaskToComplete(WaitForTaskToComplete waitForTaskToComplete) {
        this.waitForTaskToComplete = waitForTaskToComplete;
        return this;
    }
    
    public SedaUrlBuilder timeout(long timeout) {
        this.timeout = timeout;
        return this;
    }
    
    public SedaUrlBuilder multpleConsumers(boolean multipleConsumers) {
        this.multipleConsumers = multipleConsumers;
        return this;
    }
    
    public SedaUrlBuilder limitConcurrentConsumers(boolean limitConcurrentConsumers) {
        this.limitConcurrentConsumers = limitConcurrentConsumers;
        return this;
    }
    
    public SedaUrlBuilder name(String name) {
        this.name = name;
        return this;
    }
    public Integer getConcurrentConsumers() {
        return concurrentConsumers;
    }

    public void setConcurrentConsumers(int concurrentConsumers) {
        this.concurrentConsumers = concurrentConsumers;
    }

    public Boolean isLimitConcurrentConsumers() {
        return limitConcurrentConsumers;
    }

    public void setLimitConcurrentConsumers(boolean limitConcurrentConsumers) {
        this.limitConcurrentConsumers = limitConcurrentConsumers;
    }

    public Boolean isMultpleConsumers() {
        return multipleConsumers;
    }

    public void setMultpleConsumers(boolean multpleConsumers) {
        this.multipleConsumers = multpleConsumers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public WaitForTaskToComplete getWaitForTaskToComplete() {
        return waitForTaskToComplete;
    }

    public void setWaitForTaskToComplete(WaitForTaskToComplete waitForTaskToComplete) {
        this.waitForTaskToComplete = waitForTaskToComplete;
    }
    
    @Override
    public String url() {
        
        if (id == null) {
            return UrlHelper.buildUrl("seda", name, this);
        }
        
        return UrlHelper.buildUrl("seda", name + "-" + id, this);
    }
}
