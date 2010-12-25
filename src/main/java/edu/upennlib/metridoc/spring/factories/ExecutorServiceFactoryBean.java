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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.TimeUnit;
import org.apache.camel.CamelContext;
import org.apache.camel.spi.ExecutorServiceStrategy;
import org.apache.camel.spi.ThreadPoolProfile;
import org.springframework.beans.factory.FactoryBean;

/**
 *
 * TODO:add options for customization
 *
 * @author tbarker
 */
public class ExecutorServiceFactoryBean implements FactoryBean<ExecutorService>{

    private CamelContext camelContext;

    public CamelContext getCamelContext() {
        return camelContext;
    }

    public void setCamelContext(CamelContext camelContext) {
        this.camelContext = camelContext;
    }

    @Override
    public ExecutorService getObject() throws Exception {
        ExecutorServiceStrategy serviceStrategy = camelContext.getExecutorServiceStrategy();
        ThreadPoolProfile profile = serviceStrategy.getDefaultThreadPoolProfile();
        RejectedExecutionHandler executionHandler = profile.getRejectedExecutionHandler();

        return serviceStrategy.newThreadPool(this, "metridocThreadPool", 5, 10, 1L, TimeUnit.MINUTES, 50,
            executionHandler, false);
    }

    @Override
    public Class<?> getObjectType() {
        return ExecutorService.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    
}
