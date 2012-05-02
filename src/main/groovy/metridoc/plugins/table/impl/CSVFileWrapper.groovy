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
import au.com.bytecode.opencsv.CSVReader
import metridoc.plugins.table.core.*
import metridoc.plugins.Plugin

@Plugin(category="table")
@Mixin(DatasourceCategory)
class CSVFileWrapper implements SortableDatasource{
	def datasource
	def globals

	public getInputs(){
		return [
			global:["root":System.properties["user.home"]],
			instance:["file":""]
		]
	}

	private getSheetTitles(row){
		return row.collect{
			it.toString().replaceAll("[\\#\\\"\\?]+","").trim().replaceAll("\\s+","_").toLowerCase()
		}
	}

	public Object profile(){
		def filePath = getFileLocation(datasource.file)
		def inputStream = new java.io.FileReader(filePath)
		def reader = new CSVReader(inputStream)
		
		datasource.profile=buildDefaultProfile([main:getSheetTitles(reader.readNext())])
		inputStream.close()
		datasource.status = "Up"
		return datasource
	}	

	public Object each(Object builder, Object closure){
		def filePath = getFileLocation(datasource.file)
		def inputStream = new java.io.FileReader(filePath)
		def reader = new CSVReader(inputStream)
		
		def row = reader.readNext()

		def columns = getSheetTitles(row)

		while(row=reader.readNext()){
			def rowMap = [:]
			columns.eachWithIndex {coll,i ->
				rowMap[coll] = row[i]
			}
			closure(rowMap)
		}
		inputStream.close()
	}	
}

