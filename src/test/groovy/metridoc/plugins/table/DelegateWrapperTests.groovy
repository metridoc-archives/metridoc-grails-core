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
class DelegateWrapperTest extends WrapperInvariantCategory{
	def wrapped

	@Before
	public void setUp() {
		wrapped = DatasourceCategory.getInstance([type:"metridoc.plugins.table.impl.MockDatasource"])
	}

	/**
		Now the wrapped behavior tests begin
	*/
	@Test
	public void wrappedCountPennEqualsFilter(){
		def output = []
		wrapped.each([
			options:[countResults:true],
			restrictions:[[
					comparison:"=",
					value:"Penn",
					field_name:"library",
				]],
			]){
			output.add(it)
		}	
		assertEquals(1,output[0]["count(*)"])
	}

	def testIt = {chain,test,input,output ->
		def records = []

		def builder = wrapped.buildIt([:],genReport(chain),[],input)
		wrapped.each(builder){records.add(it)}	
		test(records,output)
	}

	@Test
	public void wrappedFailedEqualsRequests(){
		testIt([failedOpRequests],assertRecordCount,[operation:"="],1)
	}

	@Test
	public void wrappedFailedNotEqualsRequests(){
		testIt([failedOpRequests],assertCountAndFirstLibraryName,[operation:"!="],[8,"Brown"])
	}

	@Test
	public void wrappedFailedNotEquals1(){
		def output = []
		wrapped.each([
			restrictions:[[
					comparison:"!=",
					value:1,
					field_name:"failedReqs",
				]],
			]){
			output.add(it)
		}	
		assertEquals(5,output.size)
		assertEquals("Brown",output[0].library)
	}

	@Test
	public void wrappedValueSubstitution(){
		def output = []
		wrapped.each([
			params:[target:1],
			restrictions:[[
					comparison:"!=",
					value:"?.target",
					field_name:"failedReqs",
				]],
			]){
			output.add(it)
		}	
		assertEquals(5,output.size)
		assertEquals("Brown",output[0].library)
	}

}
