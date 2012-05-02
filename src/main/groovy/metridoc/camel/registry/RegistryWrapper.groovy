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
package metridoc.camel.registry

import org.apache.camel.spi.Registry

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 4/16/11
 * Time: 11:32 PM
 * To change this template use File | Settings | File Templates.
 */
class RegistryWrapper implements Registry {
    def List<Registry> registries = []
    def MetridocSimpleRegistry dynamicRegistry = new MetridocSimpleRegistry(new Binding())

    Object lookup(String name) {

        def service = dynamicRegistry.lookup(name)
        if(service) return service

        def response
        registries.each {Registry registry ->
            service = registry.lookup(name)
            if(service) response = service
        }

        return  response
    }

    def <T> T lookup(String name, Class<T> type) {

        def service = dynamicRegistry.lookup(name, type)
        if(service) return service

        def response
        registries.each {Registry registry ->
            service = registry.lookup(name, type)
            if(service) response = service
        }

        return response
    }

    def <T> Map<String, T> lookupByType(Class<T> type) {
        def response = [:]
        registries.each {Registry registry ->
            response.putAll registry.lookupByType(type)
        }

        response.putAll dynamicRegistry.lookupByType(type)

        return response
    }
}
