/*
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

package metridoc.camel.registry;

import groovy.lang.Binding;
import org.apache.camel.impl.SimpleRegistry;
import org.apache.camel.spi.Registry;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * Code from camel's {@link SimpleRegistry} was copied and repurposed so a hash can be provided that can be
 * manipulated outside of the camel registry
 *
 * @author Tommy Barker
 */
public class MetridocSimpleRegistry implements Registry{
    private final Binding services;

    public MetridocSimpleRegistry(Binding services) {
        this.services = services;
    }

    @Override
    public Object lookup(String name) {
        return services.getVariables().get(name);
    }

    @Override
    public <T> T lookup(String name, Class<T> type) {
        Object o = lookup(name);
        return type.cast(o);
    }

    @Override
    public <T> Map<String, T> lookupByType(Class<T> type) {
        Map<String, T> result = new HashMap<String, T>();
        Map variables = services.getVariables();
        Set keySet = variables.keySet();

        for(Object key : keySet) {
            Object value = variables.get(key);
            if(type.isInstance(value)) {
                result.put((String) key, type.cast(value));
            }
        }

        return result;
    }
}
