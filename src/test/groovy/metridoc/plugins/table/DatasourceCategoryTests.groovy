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
package metridoc.plugins.table

import static org.junit.Assert.*;
import org.junit.*
import metridoc.plugins.table.core.*
import metridoc.plugins.table.impl.*

@Mixin(ShouldFailCategory)
class DatasourceCategoryTest{
	def target

	@Before
	public void setUp() {
		target = new MySQLWrapper()
	}

	protected void tearDown() {
		super.tearDown()
	}

	@Test
	void testSetDatasource(){
		def action = target.builderActions["datasource"]
		assertEquals([datasource:"a",table:"b",params:[:]],
			action([:],[datasource:"a.b"]))
		assertEquals([datasource:"a",table:"b",params:[:]],
			action([:],[datasource:"a.b",name:"datasource"]))
		assertEquals([datasource:"a",table:"b",params:[bacon:"Rules"]],
			action([:],[datasource:"a.b",name:"datasource",bacon:"Rules"]))
	}

	@Test
	void testSetDatasourceException(){
		shouldFail(MetridocTableException){
			def action = target.builderActions["datasource"]
			assertEquals([datasource:"a",table:"b"],
				action([datasource:"Exists!"],[datasource:"a.b"]))
		}
	}

	@Test
	void testSetJoin(){
		def action = target.builderActions["join"]
		assertEquals([join:[[datasource:"a",table:"b",related_field_name:"field",join_type:"inner",original_datasource:"a.a"]]],
			action([:],[related_datasource:"a.b",related_field_name:"field",join_type:"inner",original_datasource:"a.a"]))
		assertEquals([join:[[:],[datasource:"a",table:"b",related_field_name:"field",join_type:"inner",original_datasource:"a.a"]]],
			action([join:[[:]]],[related_datasource:"a.b",related_field_name:"field",join_type:"inner",original_datasource:"a.a"]))
	}

	@Test
	void testPivot(){
		def action = target.builderActions["pivot"]
		assertEquals([pivot:[fields:["a"],target:"a",operation:"min"]],
			action([:],[pivot_fields:["a"],target_field:"a",operation:"min"]))
		assertEquals([pivot:[fields:["a","b"],target:"a",operation:"min"]],
			action([:],[pivot_fields:["a","b"],target_field:"a",operation:"min"]))
	}

	@Test
	void testPivotException(){
		shouldFail(MetridocTableException){
			def action = target.builderActions["pivot"]
			assertEquals([pivot:[fields:["a"],target:"a",operation:"min"]],
				action([pivot:"Exists!"],[pivot_fields:["a"],target_field:"a",operation:"min"]))
		}
	}

	@Test
	void testOrderBy(){
		def action = target.builderActions["order_by"]
		assertEquals([options:[order_by:"field"]],
			action([:],[field_name:"field"]))
	}

	@Test
	void testOrderByException(){
		shouldFail(MetridocTableException){
			def action = target.builderActions["order_by"]
			assertEquals([options:[order_by:"field"]],
				action([options:[order_by:"Exists!"]],[field_name:"field"]))
		}
	}

	@Test
	void testLimit(){
		def action = target.builderActions["limit"]
		assertEquals([options:[limit:10]],
			action([:],[value:10]))
	}

	@Test
	void testCount(){
		def action = target.builderActions["count"]
		assertEquals([options:[countResults:true],fields:["count(*)"]],
			action([:],[]))
		assertEquals([options:[countResults:true],fields:["count(*)"]],
			action([fields:["a","b","c"]],[]))
	}

	@Test
	void testAddField(){
		def action = target.builderActions["add_field"]
		assertEquals([fields:["a"]],
			action([:],[field_name:"a"]))
		assertEquals([fields:["a","b"]],
			action([fields:["a"]],[field_name:"b"]))
		assertEquals([fields:["a","a"]],
			action([fields:["a"]],[field_name:"a"]))
		assertEquals([fields:["count(*)"],options:[countResults:true]],
			action([fields:["count(*)"],options:[countResults:true]],[field_name:"a"]))
	}

	@Test
	void testOffset(){
		def action = target.builderActions["offset"]
		assertEquals([options:[offset:10]],
			action([:],[value:10]))
	}

	@Test
	void testRestrictField(){
		def action = target.builderActions["restrict_field"]
		assertEquals([restrictions:[[field_name:"a",comparison:"=",value:"bacon"]]],
			action([:],[field_name:"a",comparison:"=",value:"bacon"]))
		assertEquals([restrictions:[
				[field_name:"a",comparison:"=",value:"bacon"],
				[field_name:"b",comparison:"=",value:"bacon"]]],
			action([restrictions:[
				[field_name:"a",comparison:"=",value:"bacon"]]],
				[field_name:"b",comparison:"=",value:"bacon"]))
	}

	@Test
	void testInputsRequired(){
		for(entry in target.pallette){
			def action = target.builderActions[entry.key]
			
			//This is to account for pallette options that don't have a builder action,
			//such as call_report
			if(!action){
				break
			}

			//Empty Stage Fails
			shouldFail(MetridocTableException){
				action([:],[:])
			}
			for(field in entry.value.inputs){
				def fullStage = [:]
				entry.value.inputs.eachWithIndex{it,i->fullStage[it]=i}

				fullStage[field]=""
				shouldFail(MetridocTableException){
					action([:],fullStage)
				}

				fullStage[field]=" \t\r\n"
				shouldFail(MetridocTableException){
					action([:],fullStage)
				}

				fullStage.remove(field)

				//Any incomplete stage fails
				shouldFail(MetridocTableException){
					action([:],fullStage)
				}
			}
		}
	}

	@Test
	public void testGetParameter(){
		assertEquals("test",target.getParameter("\${test}"))
		assertEquals("test_underscore",target.getParameter("\${test_underscore}"))
		assertEquals(false,target.getParameter("\${}"))
		assertEquals(false,target.getParameter("fail"))
	}

	@Test
	public void testGetFieldName(){
		assertEquals("test",target.getFieldName("\$[test]"))
		assertEquals("test_underscore",target.getFieldName("\$[test_underscore]"))
		assertEquals(false,target.getFieldName("\$[]"))
		assertEquals(false,target.getFieldName("fail"))
	}

	@Test
	public void testCleanReportChain(){
		assertEquals([[a:"a"]],
			target.cleanReportChain([[a:"a"]],[:]))
		assertEquals([[a:"a"]],
			target.cleanReportChain([[a:"\${dog}"]],[dog:"a"]))
		assertEquals([[a:null]],
			target.cleanReportChain([[a:"\${dog}"]],[dog:null]))
		assertEquals([[a:"a"],[b:"a"]],
			target.cleanReportChain([[a:"\${dog}"],[b:"\${dog}"]],[dog:"a"]))
	}

	@Test
	public void testCleanJoin(){
		assertEquals([fields:["a"]],
			target.cleanJoin([fields:["a"]]))
		assertEquals([fields:["test.a"],table:"test",join:[field_name:"a"]],
			target.cleanJoin([fields:["a"],table:"test",join:[field_name:"a"]]))
		assertEquals([fields:["b"],table:"test",join:[field_name:"a"]],
			target.cleanJoin([fields:["b"],table:"test",join:[field_name:"a"]]))
		assertEquals([restrictions:[[field_name:"a"]]],
			target.cleanJoin([restrictions:[[field_name:"a"]]]))
		assertEquals([restrictions:[[field_name:"test.a"]],table:"test",join:[field_name:"a"]],
			target.cleanJoin([restrictions:[[field_name:"a"]],table:"test",join:[field_name:"a"]]))
		assertEquals([restrictions:[[field_name:"b"]],table:"test",join:[field_name:"a"]],
			target.cleanJoin([restrictions:[[field_name:"b"]],table:"test",join:[field_name:"a"]]))
	}
}
