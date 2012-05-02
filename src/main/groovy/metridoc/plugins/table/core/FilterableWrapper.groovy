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

@Mixin(DatasourceCategory)
class FilterableWrapper implements FilterableDatasource, DatasourceWrapper{
	//Delegate to the underlying wrapper
	@Delegate Datasource d

	public FilterableWrapper(d){
		this.d=d
	}

	public setDatasource(d){
		this.d.datasource=d
	}	

	public getDatasource(){
		d.datasource
	}	

	public genCompare(comparison,value,fieldName){
		def comparisonOps = [
			"=" :{row-> row[fieldName]==value},
			">" :{row-> row[fieldName]> value},
			"<" :{row-> row[fieldName]< value},
			"!=":{row-> row[fieldName]!=value},
			"<=":{row-> row[fieldName]<=value},
			">=":{row-> row[fieldName]>=value},
		]
		def otherField = getFieldName(value)
		if(otherField){
			comparisonOps = [
				"=" :{row-> row[fieldName]==row[otherField]},
				">" :{row-> row[fieldName]> row[otherField]},
				"<" :{row-> row[fieldName]< row[otherField]},
				"!=":{row-> row[fieldName]!=row[otherField]},
				"<=":{row-> row[fieldName]<=row[otherField]},
				">=":{row-> row[fieldName]>=row[otherField]},
			]
		}	
		return comparisonOps[comparison]
	}

	public conditionFactory(builder){
		def closures = []
		if(builder.restrictions){
			for(entry in builder.restrictions){
				def value = entry.value
				//Substitute the value, if required
				//Will break if the object toString method is really wonky
				value = value.toString().startsWith("?.") ? builder.params[value.toString().substring(2)] : value
				closures.add(genCompare(entry.comparison,value,entry.field_name))
			}
		}
		return {row->
			for(c in closures){
				if(!c(row)){
					return false
				}
			}
			return true
		}
	}

	public Object each(Object builder,Object closure){
		def condition = conditionFactory(builder)
		d.each(builder){row->
			if(condition(row)){
				closure(row)
			}
		}
	}
}

