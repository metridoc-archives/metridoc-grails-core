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
