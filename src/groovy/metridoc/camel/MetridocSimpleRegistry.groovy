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

package metridoc.camel;


import org.apache.camel.spi.Registry;

/**
 *
 * Code from camel's {@link org.apache.camel.impl.SimpleRegistry} was copied and repurposed so a hash can be provided that can be
 * manipulated outside of the camel registry
 *
 * @author Tommy Barker
 */
public class MetridocSimpleRegistry implements Registry {

    Binding binding

    public MetridocSimpleRegistry(Binding binding) {
        this.binding = binding;
    }

    public MetridocSimpleRegistry() {
    }

    public Object lookup(String name) {
        def object = binding.getVariables().get(name);
        def registry = getApplicationContextRegistry()
        if (object == null && registry) {
            object = registry.lookup(name)
        }

        return object
    }

    def getApplicationContextRegistry() {
        def registry
        if (binding.hasVariable("appCtx")) {
            def applicationContext = binding.appCtx
            def camelContext = applicationContext.camelContext
            if(camelContext) {
                registry = camelContext.registry
            }
        }

        return registry
    }

    public <T> T lookup(String name, Class<T> type) {
        Object o = lookup(name);
        return type.cast(o);
    }

    public <T> Map<String, T> lookupByType(Class<T> type) {
        Map<String, T> result = new HashMap<String, T>();
        def registry = getApplicationContextRegistry()
        if (registry) {
            result.putAll(registry.lookupByType(type))
        }
        Map variables = binding.getVariables();
        Set keySet = variables.keySet();

        for (Object key : keySet) {
            Object value = variables.get(key);
            if (type.isInstance(value)) {
                result.put((String) key, type.cast(value));
            }
        }

        return result;
    }
}
