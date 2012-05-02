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
import metridoc.plugins.table.core.*
import metridoc.plugins.Plugin

@Plugin(category="table")
@Mixin(DatasourceCategory)
class JSONHttpWrapper implements SortableDatasource{
	def datasource
	def globals

	public getInputs(){
		return [instance:["url":""]]
	}

	private getRawString(){
		return (new URL(datasource.url)).text
	}

	public Object profile(){
		def jsonKeys = grails.converters.JSON.parse(this.rawString)[0].keys()

		datasource.profile=buildDefaultProfile([main:jsonKeys])
		datasource.status = "Up"
		return datasource
	}	

	public Object each(Object builder, Object closure){
		def rows = grails.converters.JSON.parse(this.rawString)
		rows.each(closure)
	}	
}

