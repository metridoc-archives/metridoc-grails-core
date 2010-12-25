/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.upennlib.metridoc.routes;

import edu.upennlib.metridoc.utils.StateAssert;

/**
 *
 * @author tbarker
 */
public abstract class MetridocRouteTemplate<T extends MetridocRouteTemplate> extends MetridocRouteBuilder{

    private String startFrom;
    private String endTo;

    public T startFrom(String url) {
        startFrom = url;
        return (T) this;
    }

    public T endTo(String url) {
        endTo = url;
        return (T) this;
    }

    public String getEndTo() {
        return endTo;
    }

    public void setEndTo(String endTo) {
        this.endTo = endTo;
    }

    public String getStartFrom() {
        return startFrom;
    }

    public void setStartFrom(String startFrom) {
        this.startFrom = startFrom;
    }

    @Override
    public void validateState() throws IllegalStateException {
        StateAssert.notNull(startFrom, "startFrom needs to be specified");
        StateAssert.notNull(endTo, "endTo needs to be specified");
    }


}
