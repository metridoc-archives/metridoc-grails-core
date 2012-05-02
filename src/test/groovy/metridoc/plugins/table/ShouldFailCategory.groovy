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

class ShouldFailCategory{
	/**This is an ugly, ugly, hack.  It is designed to be used within a 
test, in order to process lots of data.
	*/
	public shouldFail(expectedEx,closure){
		try{
			closure()
			fail("Exception not thrown")
		}catch(Exception thrownEx){
			assertEquals(true,true)
		}
	}

}
