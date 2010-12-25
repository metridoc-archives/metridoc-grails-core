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
