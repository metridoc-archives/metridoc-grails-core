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
package metridoc.plugins.table.core

/**
This is kind of a potpuri Category for utility functions
*/
class DatasourceCategory{

	public static getInstance(datasource){
		getInstance(datasource,[:])
	}

	public findSQLInputs(input){
		def sqlWhitespace = "[\\s\\(\\)\\[\\]=]+"
		return input.split(sqlWhitespace).findAll{it.startsWith("?.")}
	}

	public static getInstance(datasource,globals){
		def classLoader = DatasourceCategory.classLoader
		def original = classLoader.loadClass(datasource.type).newInstance()

		original.datasource = datasource
		return DatasourceCategory.wrapInstance(original)
	}

	/**
	This method applies all the decoration required in order to ensure that
the post processing operations are performed.

	The desired order of the work is

	1. Filter the records
	2. Autoprofile, if required
	3. Pivot the results, if required (go no deeper)
	4. Count the results, if required (go no deeper)
	5. Sort the results
	6. Limit the results
	7. Return only the selected fields the results
	*/
	public static wrapInstance(original){
		def wrapped = original
		
		if(original instanceof FilterableDatasource){
			wrapped = new FilterableWrapper(wrapped)
		}
		if(original instanceof AutoprofileDatasource){
			wrapped = new AutoprofileWrapper(wrapped)
		}
		if(original instanceof PivotableDatasource){
			wrapped = new PivotableWrapper(wrapped)
		}
		if(original instanceof SortableDatasource){
			wrapped = new SortableWrapper(wrapped)
		}
		if(original instanceof CountableDatasource){
			wrapped = new CountableWrapper(wrapped)
		}
		if(original instanceof LimitableDatasource){
			wrapped = new LimitableWrapper(wrapped)
		}
		if(original instanceof SelectableDatasource){
			wrapped = new SelectableWrapper(wrapped)
		}

		return wrapped
	}

	public getFieldRegex(){
		return ~/\$\[(\w+)\]/
	}

	public getParameterRegex(){
		return ~/\$\{(\w+)\}/
	}

	public getFieldName(field){
		def m = fieldRegex.matcher(field.toString())
		if(m){
			return m[0][1]
		}else{
			return false
		}
	}

	public getParameter(field){
		def m = parameterRegex.matcher(field.toString())
		if(m){
			return m[0][1]
		}else{
			return false
		}
	}

	public getGlobalConfig(){
		output = [:]
		if(inputs.global){
			output += inputs.global
		}
		if(global){
			output += global
		}
		return output
	}

	public getFileLocation(filename){
		def root = globalConfig.root
		def sep = System.properties[""]
		def output = root + sep + filename

		return output 
	}

	public getPieCharts(){
		return ["pie_chart","ring_chart","pie_chart_3d"]
	}

	public getCharts(){
		return [] + pieCharts
	}

        public getPallette(){
		def output = [
			"datasource":[
				inputs:["datasource"],
				displayName:"Datasource",
				inputTypes:[datasource:["datasourceAutocomplete"]],
				action:{builder,stage ->
					if(builder.datasource){
						throw new MetridocTableException("Datasource is already assigned")
					} 
					def datasource = stage.datasource.split("\\.")[0]
					def table = stage.datasource.split("\\.")[1]
					builder += [
						"datasource":datasource,
						"table":table,
						]
					builder.params = builder.params ?: [:]
					stage
						.findAll{it.key!="datasource" && it.key!="name"}
						.each{entry->
							builder.params[entry.key]=entry.value
						}
					return builder
				},
				doc:'''
				This is the starting point for every report.  A datasource is a collection
				of tabular data, that can be access by metridoc.  Most datasources support 
				secondary filtering operations, but not all.
				'''
			],
			"join":[
				inputs:["related_datasource","related_field_name","join_type","original_datasource"],
				defaults:["join_type":"inner"],
				inputTypes:[
					original_datasource:["datasourceAutocomplete"],
					related_datasource:["datasourceAutocomplete"],
					related_field_name:["fieldAutocomplete"],
					join_type:["optionsAutocomplete",["inner","left outer","right outer","full outer"]],
					],
				displayName:"Join a Table",
				action:{builder,stage -> 
					builder.join = builder.join ?: []
					def datasource = stage.related_datasource.split("\\.")[0]
					def table = stage.related_datasource.split("\\.")[1]
					builder["join"]+=[
						datasource:datasource,
						table:table,
						related_field_name:stage.related_field_name,
						join_type:stage.join_type,
						original_datasource:stage.original_datasource,
						]
					return builder
				},
				doc:'''
				Joins are used to combine two normalized datasource together.  This option
				can only be used with the generic MySQL backend.
				'''
			],
			"offset":[
				inputs:["value"],
				action:{builder,stage ->
					builder.options = builder.options ?: [:]
					builder.options += ["offset":stage.value]
					return builder
				},
				doc:'''
				This stage is used to skip the first <b>value</b> records.
				'''
			],
			"limit":[
				inputs:["value"],
				action:{builder,stage ->
					builder.options = builder.options ?: [:]
					builder.options += ["limit":stage.value]
					return builder
				},
				doc:'''
				This stage is used to limit the number of returned records to <b>value</b>.
				'''
			],
			"order_by":[
				inputs:["field_name"],
				displayName:"Order By",
				inputTypes:[
					field_name:["fieldAutocomplete"],
					],
				action:{builder,stage ->
					builder.options = builder.options ?: [:]
					if(builder.options.order_by){
						throw new MetridocTableException("Order by is already assigned")
					} 
					builder.options += ["order_by":stage.field_name]
					return builder
				},
				doc:'''
				This is used to sort the fields.  You are currently limited to one field to
				sort by.  This is a very expensive operaiton, so please use it wisely.
				'''
			],
			"add_fields":[
				inputs:["add_field"],
				displayName:"Add Fields",
				inputTypes:[
					add_field:["fieldMultiSelect"],
					],
				action:{builder,stage ->
					if(!(builder.options && builder.options.countResults)){
						builder.fields = builder.fields ?: []
						builder.fields += stage.add_field
					}
					return builder
				},
				doc:'''
				This element is used to select the fields that will be displayed with the
				report.  If no fields are present, every possible field is retured (i.e. it
				behaves like SELECT *).
				'''
			],
			"pivot":[
				inputs:["pivot_fields","target_field","operation"],
				displayName:"Pivot",
				defaults:["operation":"count"],
				inputTypes:[
					pivot_fields:["fieldMultiSelect"],
					target_field:["fieldAutocomplete"],
					operation:["optionsAutocomplete",["max","min","count","total","average"]],
					],
				action:{builder,stage ->
					if(builder.pivot){
						throw new MetridocTableException("Pivot is already assigned")
					} 
					builder.pivot = [:]
					builder.pivot.fields = stage.pivot_fields
					builder.pivot.target = stage.target_field
					builder.pivot.operation = stage.operation
					return builder
				},
				doc:'''
				This is a work in progress.  It is designed to work like an excel pivot table,
				or an SQL group by statement.  However, it is not complete at this time
				'''
			],
			"add_field":[
				inputs:["field_name"],
				displayName:"Add a Lonely Field",
				inputTypes:[
					field_name:["fieldAutocomplete"],
					],
				action:{builder,stage ->
					if(!(builder.options && builder.options.countResults)){
						builder.fields = builder.fields ?: []
						builder.fields += [stage.field_name]
					}
					return builder
				},
				doc:'''
				This is used to add just one field to a report.  Its use isn't recommended any longer.
				Please upgrade to add_fields instead.
				'''
			],
			"call_report":[
				inputs:["report_name"],
				displayName:"Call Report",
				doc:'''
				This is used to call another report, and chain it together.  It is
				definitely the most powerful element here, but currently not well developed.
				'''
			],
			"restrict_field":[
				inputs:["field_name","comparison","value"],
				defaults:["comparison":"="],
				displayName:"Add a Filter",
				inputTypes:[
					field_name:["fieldAutocomplete"],
					comparison:["optionsAutocomplete",["=","<",">","<=",">=","LIKE"]],
					],
				action:{builder,stage ->
					builder.restrictions = builder.restrictions ?: []
					builder.restrictions.add(stage)
					return builder
				},
				doc:'''
				This is used for filtering the data, i.e. a WHERE clause.  There are three inputs
				available.

				The comparison input is used to determine which comparison operation can be used.  The 
				current options are =,<,>,<=,>=, and LIKE.  Note that LIKE is currently not supported
				on non-sql datasources.

				The field_name input is used to determine which field you would like to compare things too.
				You may put any column from the table here.

				The value field is what field_name get compared to.  There are three basic types of data 
				your can put here.

				The first is the hard-coded value.  Simply put in the value you'd like to hard code, and 
				metridoc will take care of the rest.  This is what you will do most of the time.

				The second type of field is the user input value.  This usefull when you want to allow a
				parameter to be chosen by the user at run time (i.e. each time the report is requested).
				In order to this, simply wrap the field name as such:

				<code>${your_field_name}</code>

				This will add the input variable to the report.

				The final type of input you can place in value is the column input value.  This is usefull
				to create a constraint between two columns in a report. You can can create a column input 
				like so:

				<code>$[column_name]</code>

				Note:  There is no way to escape out of this variable convention so if you need a ${} or $[] literal,
				it won't work.
				'''
			],
			"count":[
				inputs:[],
				action:{builder,stage ->
					builder.options = builder.options ?: [:]
					builder.options += ["countResults":true]
					builder.fields = ["count(*)"]
					return builder
				},
				doc:'''
				This is used to simply return the record count of a query.  Note that is ignores
				limit and offset stages, in order to mimic SQL.
				'''
			],
		]

		for(key in output.keySet()){
			output[key].menu="data"
		}

		//for(chart in pieCharts){
			//output[chart] = [
				//inputs:["key_field_name","value_field_name","operation"],
				//defaults:["operation":"count"],
				//inputTypes:[
					//key_field_name:["fieldAutocomplete"],
					//value_field_name:["fieldAutocomplete"],
					//operation:["optionsAutocomplete",["max","min","count","total","average"]],
					//],
				//action:{builder,stage ->
					//builder.chart = stage
					//return builder
				//},
			//]
		//}

		//for(chart in charts){
			//output[chart].menu="chart"
		//}

		for(key in output.keySet()){
			output[key].stageName = key
		}
		return output
        }

        public getBuilderActions(){
		def output = [:]

		def requireInputs = {fields,closure->
			{builder,stage ->
				for(field in fields){
					def value = stage[field]
					if(value instanceof String){
						value = value.trim()
					}
					if(value==null || value==""){
						throw new MetridocTableException("Field ${field} is missing for stage: ${stage} ")
					}
				}
				closure(builder,stage)
			}
		}

		for(entry in pallette){
			if(entry.value.action){
				output[entry.key] = requireInputs(entry.value.inputs,entry.value.action)
			}
		}

		return output 
        }

	public buildDefaultProfile(keyMap){
		def profile = [:]

		for(entry in keyMap){
			profile[entry.key] = [
				table_name:entry.key,
				columns:[],
			]
			for(column in entry.value){
				profile[entry.key].columns.add([
					column_name:column,
					type_name:"String",
					remarks:"",
				])
			}
		}
		return profile
	}

	public cleanReportChain(reportChain,inputs){
		return reportChain.collect{stage->
			def subStage= [:]
			for(entry in stage){
				if(entry.value instanceof String){
					subStage[entry.key] = getParameter(entry.value) ? inputs[getParameter(entry.value)]: entry.value
				}else{
					subStage[entry.key] = entry.value
				}
			}
			return subStage
		}
	}

	public cleanJoin(builder){
		if(builder.join){
			if(builder.fields){
				builder.fields = builder.fields.collect{
					if(it==builder.join.field_name){
						return builder.table+"."+it
					}
					return it
				} 
			}

			if(builder.restrictions){
				builder.restrictions = builder.restrictions.collect{
					if(it.field_name==builder.join.field_name){
						return it+[field_name:builder.table+"."+it.field_name]
					}
					return it
				} 
			}
		}
		return builder
	}

        public buildIt(builder,report,callStack,inputs){
		def reportChain = report.pipeline.collect{report.stages[it.toString()]}
		
		if(callStack.size()>=10){
			throw new MetridocTableException("Call stack too deep")
		}

		if(inputs){
			reportChain = cleanReportChain(reportChain,inputs)
		}

		reportChain.each{stage->
			if(stage.name=="call_report"){
				/**
					This is to get around some coupling issues with the application and the pipeline builder.
					We'll see if this is sufficient insulation
				*/
				if(!reportService){
					throw new MetridocTableException("Can not call report, no report service provided")
				}
				if(stage.report_name in callStack){
					throw new MetridocTableException("Circular dependency detected, aborting.")
				}
				def nextReport = reportService.findRecord(stage.report_name)
				builder = this.buildIt(builder,nextReport,callStack+[stage.report_name],stage)
			}else{
				builder = builderActions[stage.name](builder,stage)
			}
		}

		builder = cleanJoin(builder)
		return builder
    	}
}
