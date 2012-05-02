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
	This class inherits  the WrapperInvariant stuff to prove that
the autodetect code doesn't break the other cases
*/
@Mixin(ShouldFailCategory)
class AutoprofileTest extends WrapperInvariantCategory{
	def wrapped

	@Before
	public void setUp() {
		wrapped = DatasourceCategory.getInstance([type:"metridoc.plugins.table.impl.MockDatasource"])
		wrapped = new AutoprofileWrapper(wrapped)
	}

	def testIt = {chain,test,input,output ->
		def records = []
		//Include the sorting stuff too, in oder to test every layer
		def builder = wrapped.buildIt([:],genReport([orderByLibrary]+chain),[],input)
		wrapped.each(builder){records.add(it)}	
		test(records,output)
	}

	@Test
	public void testBasicOrdering(){
		def chain = []
		testIt(chain,assertFirstLibraryName,[:],"Brown")
	}

	@Test
	public void testOrderingWithOffset(){
		def chain = [offset1]
		testIt(chain,assertFirstLibraryName,[:],"Columbia")
	}

	@Test
	public void testOrderingWithFilter(){
		def chain = [failedOpRequests]
		testIt(chain,assertFirstLibraryName,[operation:"="],"Dartmouth")
		testIt(chain,assertFirstLibraryName,[operation:"!="],"Brown")
		testIt(chain,assertFirstLibraryName,[operation:">"],"Yale")
		testIt(chain,assertFirstLibraryName,[operation:"<"],"Brown")
		testIt(chain,assertFirstLibraryName,[operation:">="],"Dartmouth")
		testIt(chain,assertFirstLibraryName,[operation:"<="],"Brown")
	}


	@Test void testAutoprofile(){
		def chain = []
		testIt(chain,assertFirstLibraryName,[:],"Brown")
		assert(wrapped.datasource.profile)
	}	
}
