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
import org.apache.poi.hssf.usermodel.*
import metridoc.plugins.table.core.*
import metridoc.plugins.Plugin

@Plugin(category="table")
@Mixin(DatasourceCategory)
class XLSFileWrapper implements SortableDatasource{
	def datasource
	def globals

	public getInputs(){
		return [
			global:["root":System.properties["user.home"]],
			instance:["file":""]
		]
	}

	private getSheetTitles(sheet){
		return sheet.getRow(0).collect{
			it.toString().replaceAll("[\\#\\\"\\?]+","").trim().replaceAll("\\s+","_").toLowerCase()
		}
	}

	public Object profile(){
		def filePath = getFileLocation(datasource.file)
		def inputStream = new java.io.FileInputStream(filePath)
		def wb = new HSSFWorkbook(inputStream)

		def sheetCount = wb.numberOfSheets

		def xlsKeys = [:]
		def i = 0;
		for(i=0;i<sheetCount;i++){
			xlsKeys[wb.getSheetName(i)] = getSheetTitles(wb.getSheetAt(i))
		}

		inputStream.close()

		datasource.profile=buildDefaultProfile(xlsKeys)
		datasource.status = "Up"
		return datasource
	}	

	public Object each(Object builder, Object closure){
		def filePath = getFileLocation(datasource.file)
		def inputStream = new java.io.FileInputStream(filePath)
		def wb = new HSSFWorkbook(inputStream)

		def sheet = wb.getSheet(builder.table)

		def columns = getSheetTitles(sheet)

		def callClosure = null //Used to skip the headers
		sheet.each{row->
			if(callClosure){
				def rowMap = [:]
				columns.eachWithIndex {coll,i ->
					rowMap[coll] = row.getCell(i).toString()
				}
				closure(rowMap)
			}else{
				callClosure = true
			}
		}
	}	
}

