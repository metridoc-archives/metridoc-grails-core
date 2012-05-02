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
class MySQLWrapperTest{
	def target

	@Before
	public void setUp() {
		target = new MySQLWrapper()
		target.datasource = [profile:[
			test:[columns:[
				[column_name:"a",type_name:"varchar"],
				[column_name:"B",type_name:"varchar"],
				[column_name:"c",type_name:"int"],
			]],
			other_table:[columns:[
				[column_name:"a",type_name:"varchar"],
				[column_name:"d",type_name:"varchar"],
			]]
			]]//Flesh this out later...
	}

	protected void tearDown() {
		super.tearDown()
	}

	@Test
	public void testIsValidField(){
		assertEquals("a",target.isValidField("a",["a":1]))
		assertEquals("a",target.isValidField("A",["a":1]))
		assertEquals(false,target.isValidField("b",["a":1]))
		assertEquals("test.a",target.isValidField("TEST.A",["a":1]))
		assertEquals(false,target.isValidField("test.b",["a":1]))
		assertEquals(false,target.isValidField("fail.a",["a":1]))
	}

	@Test
	public void testBuildSelect(){
		assertEquals("SELECT a ",
			target.buildSelect([table:"test",fields:["a"]]))
		assertEquals("SELECT a, b ",
			target.buildSelect([table:"test",fields:["a","b"]]))
		assertEquals("SELECT * ",
			target.buildSelect([table:"test",fields:[]]))
		assertEquals("SELECT * ",
			target.buildSelect([table:"test"]))
		assertEquals("SELECT count(*) ",
			target.buildSelect([table:"test",fields:["a"],options:[countResults:true]]))
		assertEquals("SELECT count(*) ",
			target.buildSelect([table:"test",fields:[],options:[countResults:true]]))
		assertEquals("SELECT count(*) ",
			target.buildSelect([table:"test",options:[countResults:true]]))
	}

	@Test
	public void testBuildFrom(){
		assertEquals("FROM test ",
			target.buildFrom([table:"test"]))
		assertEquals("FROM test ",
			target.buildFrom([table:"test",join:[:]]))
		assertEquals("FROM test INNER JOIN other_table ON test.a=other_table.a ",
			target.buildFrom([table:"test",join:[[table:"other_table",related_field_name:"a",join_type:"inner"]]]))
		//This test is a little nonsensical, but demonstrates the string builder works
		assertEquals("FROM test INNER JOIN other_table ON test.a=other_table.a LEFT OUTER JOIN other_table ON test.a=other_table.a ",
			target.buildFrom([table:"test",join:[
				[table:"other_table",related_field_name:"a",join_type:"inner"],
				[table:"other_table",related_field_name:"a",join_type:"left outer"]]]))

	}

	@Test
	public void testBuildFromFail(){
		shouldFail(MetridocTableException){
			target.buildFrom([:])
		}
		shouldFail(MetridocTableException){
			target.buildFrom([table:"fail"])
		}
		shouldFail(MetridocTableException){
			target.buildFrom([table:"test",join:[[table:"other_table"]]])
		}
		shouldFail(MetridocTableException){
			target.buildFrom([table:"test",join:[[table:"fail",related_field_name:"a"]]])
		}
		shouldFail(MetridocTableException){
			target.buildFrom([table:"test",join:[[table:"other_table",related_field_name:"fail"]]])
		}
		shouldFail(MetridocTableException){
			target.buildFrom([table:"test",join:[[related_field_name:"a"]]])
		}
	} 

	@Test
	public void testBuildWhere(){
		assertEquals("",
			target.buildWhere([table:"test"]))
		assertEquals("WHERE a = \"text\" ",
			target.buildWhere([table:"test",restrictions:[
				[field_name:"a",comparison:"=",value:"text"],
			]]))
		assertEquals("WHERE test.a = \"text\" ",
			target.buildWhere([table:"test",restrictions:[
				[field_name:"test.a",comparison:"=",value:"text"],
			]]))
		//This test case insesitivity
		assertEquals("WHERE test.b = \"text\" ",
			target.buildWhere([table:"test",restrictions:[
				[field_name:"test.b",comparison:"=",value:"text"],
			]]))
		assertEquals("WHERE c = 10 ",
			target.buildWhere([table:"test",restrictions:[
				[field_name:"c",comparison:"=",value:"10"],
			]]))
		assertEquals("WHERE a = \"text\" AND b = \"more text\" ",
			target.buildWhere([table:"test",restrictions:[
				[field_name:"a",comparison:"=",value:"text"],
				[field_name:"b",comparison:"=",value:"more text"],
			]]))
		assertEquals("WHERE a = b ",
			target.buildWhere([table:"test",restrictions:[
				[field_name:"a",comparison:"=",value:"\$[b]"],
			]]))
		assertEquals("WHERE a = ?.urlInput ",
			target.buildWhere([table:"test",restrictions:[
				[field_name:"a",comparison:"=",value:"?.urlInput"],
			]]))
		//Determine the proper behavior here, and clean up.
		//assertEquals("WHERE a = b ",
			//target.buildWhere([table:"test",restrictions:[
				//[field_name:"a",comparison:"=",value:"\$[test.b]"],
			//]]))

		shouldFail(MetridocTableException){
			target.buildWhere([table:"test",restrictions:[
				[field_name:"d",comparison:"=",value:""],
			]])
		}
		shouldFail(MetridocTableException){
			target.buildWhere([table:"test",restrictions:[
				[field_name:"a",comparison:"=",value:"\$[d]"],
			]])
		}
		
	}

	@Test
	public void testBuildWhereWithJoin(){
		assertEquals("WHERE d = \"text\" ",
			target.buildWhere([
				table:"test",
				join:[[table:"other_table",field_name:"a"]],
				restrictions:[
					[field_name:"d",comparison:"=",value:"text"],
			]]))
		assertEquals("WHERE a = d ",
			target.buildWhere([
				table:"test",
				join:[[table:"other_table",field_name:"a"]],
				restrictions:[
					[field_name:"a",comparison:"=",value:"\$[d]"],
			]]))
	}

	@Test
	public void testBuildOptions(){
		assertEquals("ORDER BY a ;",
			target.buildOptions([table:"test",options:[order_by:"a"]]))
		assertEquals("ORDER BY test.a ;",
			target.buildOptions([table:"test",options:[order_by:"test.a"]]))
		assertEquals("LIMIT 50 ;",
			target.buildOptions([options:[limit:50]]))
		assertEquals("LIMIT 50 OFFSET 10 ;",
			target.buildOptions([options:[limit:50,offset:10]]))
			
	}

	@Test
	public void testBuildGroupBy(){
		assertEquals("GROUP BY a ",
			target.buildGroupBy([pivot:[fields:["a"]]]))
		assertEquals("GROUP BY a, b ",
			target.buildGroupBy([pivot:[fields:["a","b"]]]))
		assertEquals("",
			target.buildGroupBy([:]))
	}
}
