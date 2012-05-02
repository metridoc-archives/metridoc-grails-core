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

/**
	This class is designed to test for invariant things with the wrapper classes.
i.e. it tests the counts of various sorting operations.  It depends on having a testIt
method that defines implementation specific code.
*/
@Mixin(ShouldFailCategory)
class WrapperInvariantCategory{

	/**
		Now the wrapped behavior tests begin
	*/
	public genReport(chain){
		def report = [
			pipeline:[],
			stages:[:],
		]
		chain.eachWithIndex{link,idx ->
			report.pipeline.add(idx.toString())
			report.stages[idx.toString()]=link
		}
		return report
	}

	public genericFieldRestriction(){
		return [
		name:"restrict_field",
		comparison:"\${operation}",
		value:"\${value}",
		field_name:"\${field_name}",
	]}

	public getFailedOpRequests(){
		return[
		name:"restrict_field",
		comparison:"\${operation}",
		value:"\$[requests]",
		field_name:"failedReqs",
	]}

	public getFailedOp15(){
		return[
		name:"restrict_field",
		comparison:"\${operation}",
		value:15,
		field_name:"failedReqs",
	]}

	public getFailedEq1(){
		return[
		name:"restrict_field",
		comparison:"=",
		value:1,
		field_name:"failedReqs",
	]}

	public getLibraryOpDartmouth(){
		return[
		name:"restrict_field",
		comparison:"\${operation}",
		value:"Dartmouth",
		field_name:"library",
	]}

	public getCountOperation(){
		return[
		name:"count",
	]}

	public getAddLibrary(){
		return[
		name:"add_field",
		field_name:"library"
	]}

	public getAddRequests(){
		return[
		name:"add_field",
		field_name:"requests"
	]}

	public getAddFailedReqs(){
		return[
		name:"add_field",
		field_name:"failedReqs"
	]}

	public getLimit0(){
		return[
		name:"limit",
		value:0
	]}

	public getLimit3(){
		return[
		name:"limit",
		value:3
	]}

	public getLimit10(){
		return[
		name:"limit",
		value:10
	]}

	public getOffset0(){
		return[
		name:"offset",
		value:0
	]}

	public getOffset1(){
		return[
		name:"offset",
		value:1
	]}

	public getOffset10(){
		return[
		name:"offset",
		value:10
	]}

	public getOrderByLibrary(){
		return[
		name:"order_by",
		field_name:"library"
	]}

	public getAssertRecordCount(){
		return {records,expected ->
			assertEquals(expected,records.size)
		}
	}

	//[>*
		//This is for determining the order as well as count for an operation
	//*/
	public getAssertCountAndFirstLibraryName(){
		return {records,expected ->
			assertEquals(expected[0],records.size)
			assertEquals(expected[1],records[0].library)
		}
	}

	public getAssertCountOperation(){ 
		return {records,expected ->
			assertEquals(expected,records[0]["count(*)"])
		}
	}

	public getAssertFieldCount(){
		return  {records,expected ->
			assertEquals(expected,records[0].size())
		}
	}

	public getAssertFirstLibraryName(){
		return  {records,expected ->
			assertEquals(expected,records[0].library)
		}
	}

	/**
		The following tests ensure that the wrapped datasource behaves like
	the mock source when no options are presented
	*/
	@Test
	public void wrappedProfileSetup(){
		assertEquals(3,wrapped.profile().profile.main_report.columns.size)
		assertEquals("Fake",wrapped.profile().profile.main_report.table_name)
	}

	@Test
	public void wrappedFieldCount(){
		testIt([offset0],assertFieldCount,[:],3)
		testIt([offset0,addLibrary],assertFieldCount,[:],1)
		testIt([offset0,addLibrary,addRequests],assertFieldCount,[:],2)
		testIt([offset0,addLibrary,addRequests,addFailedReqs],assertFieldCount,[:],3)
	}

	@Test
	public void wrappedFieldCompareBulk(){
		def chain = [failedOpRequests]
		testIt(chain,assertRecordCount,[operation:"="],1)
		testIt(chain,assertRecordCount,[operation:"!="],8)
		testIt(chain,assertRecordCount,[operation:">"],1)
		testIt(chain,assertRecordCount,[operation:"<"],7)
		testIt(chain,assertRecordCount,[operation:">="],2)
		testIt(chain,assertRecordCount,[operation:"<="],8)
	}

	@Test
	public void wrappedStringCompareBulk(){
		def chain = [libraryOpDartmouth]
		testIt(chain,assertRecordCount,[operation:"="],1)
		testIt(chain,assertRecordCount,[operation:"!="],8)
		testIt(chain,assertRecordCount,[operation:">"],5)
		testIt(chain,assertRecordCount,[operation:"<"],3)
		testIt(chain,assertRecordCount,[operation:">="],6)
		testIt(chain,assertRecordCount,[operation:"<="],4)
	}

	@Test
	public void wrappedStringCompareCompoundBulk(){
		def chain = [libraryOpDartmouth,failedEq1]
		testIt(chain,assertRecordCount,[operation:"="],1)
		testIt(chain,assertRecordCount,[operation:"!="],3)
		testIt(chain,assertRecordCount,[operation:">"],2)
		testIt(chain,assertRecordCount,[operation:"<"],1)
		testIt(chain,assertRecordCount,[operation:">="],3)
		testIt(chain,assertRecordCount,[operation:"<="],2)
	}

	@Test
	public void wrappedFieldCompareBulkWithLimit(){
		def chain = [failedOpRequests,limit0]
		testIt(chain,assertRecordCount,[operation:"="],0)
		testIt(chain,assertRecordCount,[operation:"!="],0)
		testIt(chain,assertRecordCount,[operation:">"],0)
		testIt(chain,assertRecordCount,[operation:"<"],0)
		testIt(chain,assertRecordCount,[operation:">="],0)
		testIt(chain,assertRecordCount,[operation:"<="],0)

		chain = [failedOpRequests,limit3]
		testIt(chain,assertRecordCount,[operation:"="],1)
		testIt(chain,assertRecordCount,[operation:"!="],3)
		testIt(chain,assertRecordCount,[operation:">"],1)
		testIt(chain,assertRecordCount,[operation:"<"],3)
		testIt(chain,assertRecordCount,[operation:">="],2)
		testIt(chain,assertRecordCount,[operation:"<="],3)

		chain = [failedOpRequests,limit10]
		testIt(chain,assertRecordCount,[operation:"="],1)
		testIt(chain,assertRecordCount,[operation:"!="],8)
		testIt(chain,assertRecordCount,[operation:">"],1)
		testIt(chain,assertRecordCount,[operation:"<"],7)
		testIt(chain,assertRecordCount,[operation:">="],2)
		testIt(chain,assertRecordCount,[operation:"<="],8)
	}

	@Test
	public void wrappedFieldCompareBulkWithOffset(){
		def chain = [failedOpRequests,offset0]
		testIt(chain,assertRecordCount,[operation:"="],1)
		testIt(chain,assertRecordCount,[operation:"!="],8)
		testIt(chain,assertRecordCount,[operation:">"],1)
		testIt(chain,assertRecordCount,[operation:"<"],7)
		testIt(chain,assertRecordCount,[operation:">="],2)
		testIt(chain,assertRecordCount,[operation:"<="],8)

		chain = [failedOpRequests,offset1]
		testIt(chain,assertRecordCount,[operation:"="],0)
		testIt(chain,assertRecordCount,[operation:"!="],7)
		testIt(chain,assertRecordCount,[operation:">"],0)
		testIt(chain,assertRecordCount,[operation:"<"],6)
		testIt(chain,assertRecordCount,[operation:">="],1)
		testIt(chain,assertRecordCount,[operation:"<="],7)

		chain = [failedOpRequests,offset10]
		testIt(chain,assertRecordCount,[operation:"="],0)
		testIt(chain,assertRecordCount,[operation:"!="],0)
		testIt(chain,assertRecordCount,[operation:">"],0)
		testIt(chain,assertRecordCount,[operation:"<"],0)
		testIt(chain,assertRecordCount,[operation:">="],0)
		testIt(chain,assertRecordCount,[operation:"<="],0)
	}

	@Test
	public void wrappedFieldCompareBulkWithLimitAndOffset(){

		def processIt = {inputChain,ops ->
			ops.each{
				testIt(inputChain,assertRecordCount,[operation:it.key],it.value)
			}
		}

		def chain = [failedOpRequests,offset0,limit10]
		processIt(chain,["=":1,"!=":8,">":1,"<":7,">=":2,"<=":8])

		chain = [failedOpRequests,offset1,limit10]
		processIt(chain,["=":0,"!=":7,">":0,"<":6,">=":1,"<=":7])

		chain = [failedOpRequests,offset10,limit10]
		processIt(chain,["=":0,"!=":0,">":0,"<":0,">=":0,"<=":0])

		chain = [failedOpRequests,offset0,limit3]
		processIt(chain,["=":1,"!=":3,">":1,"<":3,">=":2,"<=":3])

		chain = [failedOpRequests,offset1,limit3]
		processIt(chain,["=":0,"!=":3,">":0,"<":3,">=":1,"<=":3])

		chain = [failedOpRequests,offset10,limit3]
		processIt(chain,["=":0,"!=":0,">":0,"<":0,">=":0,"<=":0])

		chain = [failedOpRequests,offset0,limit0]
		processIt(chain,["=":0,"!=":0,">":0,"<":0,">=":0,"<=":0])

		chain = [failedOpRequests,offset1,limit0]
		processIt(chain,["=":0,"!=":0,">":0,"<":0,">=":0,"<=":0])

		chain = [failedOpRequests,offset10,limit0]
		processIt(chain,["=":0,"!=":0,">":0,"<":0,">=":0,"<=":0])
	}

	@Test
	public void wrappedFailedValueCompareBulk(){
		def chain = [failedOp15]
		testIt(chain,assertRecordCount,[operation:"="],1)
		testIt(chain,assertRecordCount,[operation:"!="],8)
		testIt(chain,assertRecordCount,[operation:">"],3)
		testIt(chain,assertRecordCount,[operation:"<"],5)
		testIt(chain,assertRecordCount,[operation:">="],4)
		testIt(chain,assertRecordCount,[operation:"<="],6)
	}

	@Test
	public void wrappedBasicCountResults(){
		def chain = [countOperation]
		testIt(chain,assertCountOperation,[:],9)
		testIt(chain,assertRecordCount,[:],1)
	}

	@Test
	public void wrappedAdvanceCountResults(){

		def processIt = {inputChain,ops ->
			ops.each{
				testIt(inputChain,assertCountOperation,[operation:it.key],it.value)
			}
		}

		def chain = [failedOpRequests,countOperation]
		processIt(chain,["=":1,"!=":8,">":1,"<":7,">=":2,"<=":8])

		/**
			Notice how the result never changes!  This is because
		the count operatio was designed to mimic SQL, and the SQL count
		operation ignores limit and offset.
		*/
		chain = [failedOpRequests,offset0,limit10,countOperation]
		processIt(chain,["=":1,"!=":8,">":1,"<":7,">=":2,"<=":8])

		chain = [failedOpRequests,offset1,limit10,countOperation]
		processIt(chain,["=":1,"!=":8,">":1,"<":7,">=":2,"<=":8])

		chain = [failedOpRequests,offset10,limit10,countOperation]
		processIt(chain,["=":1,"!=":8,">":1,"<":7,">=":2,"<=":8])

		chain = [failedOpRequests,offset0,limit3,countOperation]
		processIt(chain,["=":1,"!=":8,">":1,"<":7,">=":2,"<=":8])

		chain = [failedOpRequests,offset1,limit3,countOperation]
		processIt(chain,["=":1,"!=":8,">":1,"<":7,">=":2,"<=":8])

		chain = [failedOpRequests,offset10,limit3,countOperation]
		processIt(chain,["=":1,"!=":8,">":1,"<":7,">=":2,"<=":8])

		chain = [failedOpRequests,offset0,limit0,countOperation]
		processIt(chain,["=":1,"!=":8,">":1,"<":7,">=":2,"<=":8])

		chain = [failedOpRequests,offset1,limit0,countOperation]
		processIt(chain,["=":1,"!=":8,">":1,"<":7,">=":2,"<=":8])

		chain = [failedOpRequests,offset10,limit0,countOperation]
		processIt(chain,["=":1,"!=":8,">":1,"<":7,">=":2,"<=":8])
	}

}
