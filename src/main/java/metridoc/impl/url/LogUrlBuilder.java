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

package metridoc.impl.url;

import metridoc.url.UrlBuilder;
import metridoc.url.UrlHelper;
import metridoc.url.UrlParam;
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
