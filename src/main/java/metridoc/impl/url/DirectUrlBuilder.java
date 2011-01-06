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
