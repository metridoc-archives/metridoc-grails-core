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
package metridoc.plugins.table.impl
import groovy.sql.Sql
import metridoc.plugins.table.core.*
import org.apache.commons.dbcp.BasicDataSource;
import metridoc.plugins.Plugin

/**
This datasoruce is designed to be used when the SQL statement is too complex to
generate using the pallette.  The main case when this happends is when you are
using aggregate functions in the SQL.  However, it is recommended that you
try to create a view in you database first, before using this object.

For your own safety, please only use select statements with this object.

The parameters are specified with groovy style parameters, as described in the
documentation for groovy.sql.Sql.  That is to say, they are prefaced with "?."
and then the field name.  For example,

  SELECT * FROM foo WHERE bar=?.parameter_name

This will expose parameter name as a 

*/
@Plugin(category="table")
@Mixin(DatasourceCategory)
class SQLStatementDatasource implements SortableDatasource, AutoprofileDatasource{
	def datasource
	def globals

	static sqlPool = [:]

	public getSQLDatasource(){
		def sqlDatasource = null
		def hash = datasource.url + datasource.username+datasource.password
		if(!sqlPool[hash]){
			sqlDatasource = new BasicDataSource();
			sqlDatasource.setDriverClassName("com.mysql.jdbc.Driver");
			sqlDatasource.setUsername(datasource.username);
			sqlDatasource.setPassword(datasource.password);
			sqlDatasource.setDefaultReadOnly(true);
			sqlDatasource.setPoolPreparedStatements(true);
			sqlDatasource.setUrl("jdbc:"+datasource.url);
			sqlPool[hash] = sqlDatasource
		}
		return sqlPool[hash]
	}

	public getInputs(){
		def output = [:]
		output += [instance:
				[url:"mysql://host-name:3306/database-name",
				username:"name",
				password:"secret",
				statement:"SELECT * FROM foo"]]
		if(datasource){
			output += [request:[:]]
			def tokens = findSQLInputs(datasource.statement)
			tokens.each{token->
				output.request[token.substring(2)]="\${${token.substring(2)}}".toString()
			}
		}
		return output
	}

	//This is an auto-profiling datasource
	public Object profile(){
		datasource.profile=buildDefaultProfile([main:[]])
		datasource.status = "Up"
		return datasource
	}

	public Object each(Object builder, Object c){
		println builder
		def sql = new Sql(this.getSQLDatasource())
		sql.withStatement{it.fetchSize=Integer.MIN_VALUE}

		def positionalStatement =  datasource.statement.replaceAll("\\?\\.\\w+","?")
		def sqlParams = getInputs().request.collect{builder.params[it.key]}

		if(this.inputs.request){
			sql.eachRow(positionalStatement,sqlParams,{row->c(row.toRowResult())})
		}else{
			sql.eachRow(datasource.statement,{row->c(row.toRowResult())})
		}
	}
}
