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

package edu.upennlib.metridoc.spring.factories;

import edu.upennlib.metridoc.config.Environment;
import java.util.Map;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 *
 * @author tbarker
 */
public class EnvironmentFactoryBean implements FactoryBean, ApplicationContextAware{

    private Map<Environment, String> serviceMap;
    private Class type;
    private ApplicationContext applicationContext;

    public Class getType() {
        return type;
    }

    public void setType(Class type) {
        this.type = type;
    }

    public void setServiceMap(Map<Environment, String> serviceMap) {
        this.serviceMap = serviceMap;
    }

    protected Map<Environment, String> getServiceMap() {
        return serviceMap;
    }

    @Override
    public Object getObject() throws Exception {
        Environment env = Environment.get();
        String serviceName = serviceMap.get(env);
        return applicationContext.getBean(serviceName, type);
    }

    @Override
    public Class<?> getObjectType() {
        return type;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
