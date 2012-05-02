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

@Plugin(category="table")
@Mixin(DatasourceCategory)
class MySQLWrapper implements Datasource{
	def datasource
	def globals

	static sqlPool = [:]

	public getSQLDatasource(){
		def sqlDatasource = null
		def hash = datasource.url + datasource.username+datasource.password
		def setGlobals = [:]
		if(inputs.global){
			setGlobals += inputs.global
		}
		if(globals){
			setGlobals += globals
		}
		if(!sqlPool[hash]){
			sqlDatasource = new BasicDataSource();
			sqlDatasource.setDriverClassName("com.mysql.jdbc.Driver");
			sqlDatasource.setUsername(datasource.username);
			sqlDatasource.setPassword(datasource.password);
			sqlDatasource.setDefaultReadOnly(setGlobals.defaultReadOnly);
			sqlDatasource.setPoolPreparedStatements(setGlobals.defaultReadOnly);
			sqlDatasource.setMaxActive(setGlobals.maxActive )
			sqlDatasource.setMaxIdle(setGlobals.maxIdle )
			sqlDatasource.setMinIdle(setGlobals.minIdle )
			sqlDatasource.setInitialSize(setGlobals.initialSize )
			sqlDatasource.setMinEvictableIdleTimeMillis(setGlobals.minEvictableIdleTimeMillis )
			sqlDatasource.setTimeBetweenEvictionRunsMillis(setGlobals.timeBetweenEvictionRunsMillis )
			sqlDatasource.setMaxWait(setGlobals.maxWait )
			sqlDatasource.setValidationQuery(setGlobals.validationQuery )
			sqlDatasource.setTestOnBorrow(setGlobals.testOnBorrow)
			sqlDatasource.setUrl("jdbc:"+datasource.url);
			sqlPool[hash] = sqlDatasource
		}
		return sqlPool[hash]
	}

	public getInputs(){
		return [instance:["url":"mysql://host-name:3306/database-name","username":"name","password":"secret"],
			global:[
				defaultReadOnly:true,
				poolPreparedStatements:true,
				maxActive : 25,
				maxIdle : 15,
				minIdle : 3,
				initialSize : 3,
				minEvictableIdleTimeMillis : 60000,
				timeBetweenEvictionRunsMillis : 60000,
				maxWait : 10000,
				validationQuery : "select 1",
				testOnBorrow:true,
			]]
	}

	private createCon(datasource){
		Class.forName("com.mysql.jdbc.Driver")
		def con = java.sql.DriverManager.getConnection(
			"jdbc:"+datasource.url,
			datasource.username,
			datasource.password
		)
		return con
	}

	public Object profile(){
		try{
			def con = this.getSQLDatasource().getConnection()
			def metadata = con.metaData
			def tableInfo =  [:]

			metadata.getTables("","","",null).toMaps().each{
				it.columns = []
				tableInfo[it.table_name]=it
			}

			metadata.getColumns("","","","").toMaps().each{
				tableInfo[it.table_name].columns.add(it)
			}

			datasource.profile = tableInfo
			datasource.status = "Up"
			datasource.remove("error")
		
		}
		catch(Exception e){
			//e.stackTrace.each{println it}
			datasource.error = "There was an error connecting to the database."
			datasource.status = "Down"
			datasource.remove("profile")
		}
		return datasource
	}

	public buildSelect(builder){
		def statement = new StringBuilder()
		def columnCache = this.getColumnLookup(builder.table,[:])
		statement.append("SELECT ")

		if(builder.options && builder.options.countResults){
			statement.append("count(*) ")
		}else if(builder.fields){
			statement.append(builder.fields.join(", ")+" ")
		}else{
			statement.append("* ")
		}

		return statement.toString()

	}

	public buildFrom(builder){
		def statement = new StringBuilder()

		if(!builder.table){
			throw new MetridocTableException("Datasource table is missing")
		}

		if(!datasource.profile[builder.table.toLowerCase()]){
			throw new MetridocTableException("Table ${builder.table} does not exist for this datasource")
		}

		statement.append("FROM ${builder.table} ")

		if(builder.join){
			def columnCache = this.getColumnLookup(builder.table,[:])
			builder.join.each{join_target ->
				if(!join_target.table){
					throw new MetridocTableException("Join table is missing")
				}
				if(!datasource.profile[join_target.table.toLowerCase()]){
					throw new MetridocTableException("Table ${builder.join.table} does not exist for this datasource")
				}
				if(!join_target.related_field_name){
					throw new MetridocTableException("Join field name is missing")
				}
				if(!isValidField(join_target.related_field_name,columnCache)){
					throw new MetridocTableException("The field ${builder.join.related_field_name} doesn't exist.")
				}
				statement.append("${join_target.join_type.toUpperCase()} JOIN ${join_target.table} ")
				statement.append("ON ${builder.table}.${join_target.related_field_name}=${join_target.table}.${join_target.related_field_name} ")
			}
		}
		return statement.toString()
	}

	public buildWhere(builder){
		def statement = new StringBuilder()

		if(builder.restrictions){
			def columnCache = this.getColumnLookup(builder.table,[:])
			if(builder.join){
				builder.join.each{j->
					columnCache = this.getColumnLookup(j.table,columnCache)
				}
			}
			statement.append("WHERE ")
			statement.append(builder.restrictions.collect{entry ->
				def output = ""
				if(isValidField(entry.field_name,columnCache)){
					output += isValidField(entry.field_name,columnCache)
				}else{
					throw new MetridocTableException("Column \"${entry.field_name}\" doesn't exist.")
				}
				output += " ${entry.comparison}"
				if(entry.value.startsWith("?.")){
					builder["_requireParams"] = true
					output += " ${entry.value}"
				}else if(getFieldName(entry.value)){
					if(!isValidField(getFieldName(entry.value),columnCache)){
						throw new MetridocTableException("Column \"${getFieldName(entry.field_name)}\" doesn't exist.")
					}
					output += " ${getFieldName(entry.value)}"
				}else if(columnCache[entry.field_name.split("\\.")[-1]].type_name.toLowerCase().contains("char")){
					output += " \"${entry.value}\""
				}else{
					output += " ${entry.value}"
				}
				return output
			}.join(" AND ") + " ")
		}
		return statement.toString()
	}

	public buildGroupBy(builder){
		def statement = new StringBuilder()
		if(builder.pivot){
			statement.append("GROUP BY ")
			statement.append(builder.pivot.fields.join(", "))
			statement.append(" ")
		}
		return statement.toString()
	}

	public buildOptions(builder){
		def statement = new StringBuilder()

		if(builder.options.order_by){
			def columnCache = this.getColumnLookup(builder.table,[:])
			if(builder.join){
				builder.join.each{j->
					columnCache = this.getColumnLookup(j.table,columnCache)
				}
			}
			if(!isValidField(builder.options.order_by,columnCache)){
				throw new MetridocTableException("Column \"${getFieldName(entry.field_name)}\" doesn't exist.")
			}
			statement.append("ORDER BY ${builder.options.order_by} ")
		}
		if(builder.options.limit){
			statement.append("LIMIT ${builder.options.limit} ")
		}
		if(builder.options.offset){
			statement.append("OFFSET ${builder.options.offset} ")
		}
		
		statement.append(";")
		return statement.toString()
	}

	public getColumnLookup(table,cache){
		def tableInfo = datasource.profile[table]
		tableInfo.columns.each{
			cache[it["column_name"].toLowerCase()]=it
		}
		return cache
	}

	public isValidField(field,cache){
		if(cache[field.toLowerCase()]){
			return field.toLowerCase()
		}
		if(!field.contains(".")){
			return false
		}
		def entries = field.toLowerCase().split("\\.")
		if(cache[entries[-1]] && datasource.profile[entries[-2]]){
			return entries[-2]+"."+entries[-1]
		}

		return false
	}

	public buildSql(builder){
		def statement = new StringBuilder()
		def tableInfo = datasource.profile[builder.table]

		statement.append(buildSelect(builder))

		statement.append(buildFrom(builder))

		statement.append(buildWhere(builder))

		statement.append(buildGroupBy(builder))

		statement.append(buildOptions(builder))

		return statement.toString()
	}

	public Object each(Object builder, Object c){
		def statement = this.buildSql(builder)
		def sql = new Sql(this.getSQLDatasource())
		sql.withStatement{it.fetchSize=Integer.MIN_VALUE}

		if(builder["_requireParams"]){
			def positionalStatement =  statement.replaceAll("\\?\\.\\w+","?")
			def inputs = findSQLInputs(statement)
			def sqlParams = inputs.collect{builder.params[it.substring(2)]}

			sql.eachRow(positionalStatement,sqlParams,{row->c(row.toRowResult())})
		}else{
			sql.eachRow(statement,{row->c(row.toRowResult())})
		}
	}
}
