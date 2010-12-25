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
