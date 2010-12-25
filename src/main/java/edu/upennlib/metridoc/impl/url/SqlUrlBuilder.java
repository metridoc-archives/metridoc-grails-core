/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.upennlib.metridoc.impl.url;

import edu.upennlib.metridoc.url.UrlBuilder;
import edu.upennlib.metridoc.url.UrlHelper;
import edu.upennlib.metridoc.url.UrlParam;

/**
 *
 * @author tbarker
 */
public class SqlUrlBuilder implements UrlBuilder{

    private String dataSourceRef;
    @UrlParam(include=false)
    private String sql;

    public String getDataSourceRef() {
        return dataSourceRef;
    }

    public SqlUrlBuilder sql(String sql) {
        this.sql = sql;
        return this;
    }
    
    public SqlUrlBuilder dataSourceRef(String dataSourceRef) {
        this.dataSourceRef = dataSourceRef;
        return this;
    }
    
    public void setDataSourceRef(String dataSourceRef) {
        this.dataSourceRef = dataSourceRef;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    @Override
    public String url() {
        String url = UrlHelper.buildUrl("sql", "<sqlPlaceholder>", this);
        String formattedSql = sql.replaceAll("\\?", "#");
        
        return url.replace("<sqlPlaceholder>", formattedSql);
    }
}
