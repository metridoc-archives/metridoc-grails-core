/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.upennlib.metridoc.impl.url;

import edu.upennlib.metridoc.url.PipelineBuilder;

/**
 *
 * @author tbarker
 */
public class SqlPipelineBuilder implements PipelineBuilder {

    private String dataSourceRef;
    private String[] sqlQueries;
    
    public SqlPipelineBuilder execute(String... queries) {
        sqlQueries = queries;
        return this;
    }
    
    public SqlPipelineBuilder dataSourceRef(String dataSourceRef) {
        this.dataSourceRef = dataSourceRef;
        return this;
    }
    
    @Override
    public String[] urls() {
        
        String result[] = new String[sqlQueries.length];
        
        for (int i = 0; i < result.length; i++) {
            result[i] = new SqlUrlBuilder().dataSourceRef(dataSourceRef).sql(sqlQueries[i]).url();
        }
        
        return result;
    }

}
