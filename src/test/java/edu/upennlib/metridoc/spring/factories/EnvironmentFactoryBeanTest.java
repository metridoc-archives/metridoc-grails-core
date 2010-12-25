/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.upennlib.metridoc.spring.factories;

import edu.upennlib.metridoc.spring.factories.EnvironmentFactoryBean;
import edu.upennlib.metridoc.config.Environment;
import java.util.EnumMap;
import java.util.Map;
import org.springframework.context.ApplicationContext;
import javax.sql.DataSource;
import org.junit.Assert;
import org.junit.Test;
import static org.easymock.EasyMock.*;

/**
 *
 * Originally this was built for 
 * @author tbarker
 */
public class EnvironmentFactoryBeanTest extends Assert{

    private DataSource testDataSource;
    private DataSource prodDataSource;
    private EnvironmentFactoryBean factoryBean;
    private ApplicationContext applicationContext;

    @Test
    public void getObjectReturnsDataSourceBasedOnEnvironment() throws Exception {
        replay(applicationContext());
        Environment.set(Environment.TEST);
        assertEquals(testDataSource(), factoryBean().getObject());
        Environment.set(Environment.PRODUCTION);
        assertEquals(prodDataSource(), factoryBean().getObject());
        verify(applicationContext());

    }

    public EnvironmentFactoryBean factoryBean() {

        if (factoryBean == null) {
            factoryBean = new EnvironmentFactoryBean();
            factoryBean.setType(DataSource.class);
            Map<Environment, String> serviceMap = new EnumMap<Environment, String>(Environment.class);
            serviceMap.put(Environment.TEST, "testDataSource");
            serviceMap.put(Environment.PRODUCTION, "prodDataSource");
            factoryBean.setServiceMap(serviceMap);
            factoryBean.setApplicationContext(applicationContext());
        }

        return factoryBean;
    }

    public ApplicationContext applicationContext() {
        if (applicationContext == null) {
            applicationContext = createMock(ApplicationContext.class);
            expect(applicationContext.getBean("testDataSource", DataSource.class)).andReturn(testDataSource()).once();
            expect(applicationContext.getBean("prodDataSource", DataSource.class)).andReturn(prodDataSource()).once();
        }

        return applicationContext;
    }

    public DataSource testDataSource() {
        if (testDataSource == null) {
            testDataSource = createMock(DataSource.class);
        }

        return testDataSource;
    }

    public DataSource prodDataSource() {
        if (prodDataSource == null) {
            prodDataSource = createMock(DataSource.class);
        }

        return prodDataSource;
    }

}