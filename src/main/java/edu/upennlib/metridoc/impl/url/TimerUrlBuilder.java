/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.upennlib.metridoc.impl.url;

import edu.upennlib.metridoc.url.UrlBuilder;
import edu.upennlib.metridoc.url.UrlHelper;
import edu.upennlib.metridoc.url.UrlParam;
import org.apache.commons.lang.RandomStringUtils;

/**
 *
 * @author Thomas Barker
 */
public class TimerUrlBuilder implements UrlBuilder{
    
    @UrlParam(include = false)
    private String name = RandomStringUtils.randomAlphanumeric(6);
    private String time;
    private String pattern;
    private Long period;
    private Long delay;
    private Boolean fixedRate;
    private Boolean daemon;

    public TimerUrlBuilder daily() {
        period = 1000L * 60 * 60 * 24;
        fixedRate = true;
        return this;
    }
    
    public TimerUrlBuilder daemon(boolean daemon) {
        this.daemon = daemon;
        return this;
    }
    
    public Boolean getDaemon() {
        return daemon;
    }

    public void setDaemon(Boolean daemon) {
        this.daemon = daemon;
    }

    public TimerUrlBuilder delay(long delay) {
        this.delay = delay;
        return this;
    }
    
    public Long getDelay() {
        return delay;
    }

    public void setDelay(Long delay) {
        this.delay = delay;
    }

    public TimerUrlBuilder fixedRate(boolean fixedRate) {
        this.fixedRate = fixedRate;
        return this;
    }

    public Boolean getFixedRate() {
        return fixedRate;
    }

    public void setFixedRate(Boolean fixedRate) {
        this.fixedRate = fixedRate;
    }

    public TimerUrlBuilder name(String name) {
        this.name = name;
        return this;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TimerUrlBuilder pattern(String pattern) {
        this.pattern = pattern;
        return this;
    }
    
    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public TimerUrlBuilder period(long period) {
        this.period = period;
        return this;
    }
    
    public Long getPeriod() {
        return period;
    }

    public void setPeriod(Long period) {
        this.period = period;
    }

    public TimerUrlBuilder time(String time) {
        this.time = time;
        return this;
    }
    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String url() {
        return UrlHelper.buildUrl("timer", name, this);
    }
}
