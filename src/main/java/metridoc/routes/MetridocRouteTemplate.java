/**
 * Copyright 2010 Trustees of the University of Pennsylvania Licensed under the
 * Educational Community License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package metridoc.routes;

import metridoc.utils.StateAssert;

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
