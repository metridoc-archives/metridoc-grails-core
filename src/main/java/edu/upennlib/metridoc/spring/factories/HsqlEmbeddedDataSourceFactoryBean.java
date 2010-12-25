/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.upennlib.metridoc.spring.factories;

import javax.sql.DataSource;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

/**
 *
 * HSQL embedded database factory bean
 *
 * @author tbarker
 */
public class HsqlEmbeddedDataSourceFactoryBean implements FactoryBean<DataSource>{

    @Override
    public DataSource getObject() throws Exception {
        return new EmbeddedDatabaseBuilder().build();
    }

    @Override
    public Class<?> getObjectType() {
        return DataSource.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
