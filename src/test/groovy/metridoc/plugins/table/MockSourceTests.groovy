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
class MockSourceTest{
	def mock

	@Before
	public void setUp() {
		mock = new MockDatasource()
		mock.datasource = [:]
	}

	@Test
	public void isMockActive(){
		assertEquals(true,mock!=null)
	}

	@Test
	public void mockProfileSetup(){
		assertEquals(3,mock.profile().profile.main_report.columns.size)
		assertEquals("Fake",mock.profile().profile.main_report.table_name)
	}

	@Test
	public void mockInputSetup(){
		assertEquals([:],mock.inputs)
	}

	@Test
	public void mockEachNoInput(){
		def output = []
		mock.each([:]){
			output.add(it)
		}	
		assertEquals(9,output.size)
		assertEquals('Brown',output[0].library)
		assertEquals('Columbia',output[1].library)
	}

	@Test
	public void mockEachWithInput(){
		def output = []
		mock.each([:]){
			output.add(it)
		}	
		assertEquals('Brown',output[0].library)
		assertEquals('Columbia',output[1].library)
	}

}
