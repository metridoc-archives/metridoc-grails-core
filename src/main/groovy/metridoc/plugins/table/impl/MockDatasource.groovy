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

/**
	This class is used as a mock datasource for testing purposes.  This applies
to both unit tests, and individuals that want a predictable datasource out
of the box

author : Narine Ghochikyan
author : Sean Devlin
 */

@Plugin(category="table")
class MockDatasource implements SortableDatasource{
	def datasource 
	def globals
	
	public Object getInputs() {
		//return [request:['config_param':'']];
		return [:]
	}

	public Object profile() {
		def profile = [
			main_report:
			[table_name: 'Fake', 
			columns: [
				[column_name:'library',
				type_name:"String"],
				[column_name:'requests',
				type_name:"int"],
				[column_name:'failedReqs',
				type_name:"int"]
			]]	
		]
		
		datasource.profile = profile
		return datasource;
	}

	public Object each(Object builder, Object closure) {
		def data = [makeFakeRow('Brown', 200, 20),
			makeFakeRow('Columbia', 180, 15), 
			makeFakeRow('Penn', 215, 30),
			makeFakeRow('Cornell', 45, 1),
			makeFakeRow('Princeton', 95, 1),
			makeFakeRow('MIT', 105, 1),
			makeFakeRow('Yale', 98, 890),
			makeFakeRow('Harvard', 68, 12),
			makeFakeRow('Dartmouth', 1, 1)]
	
		return data.each(closure);
	}
	
	private makeFakeRow(lib, reqs, failedReqs){
		return [library: lib, requests: reqs, failedReqs: failedReqs]
	}
}
