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
package metridoc.workflows

import org.junit.Test

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 3/2/12
 * Time: 4:46 AM
 */
class IteratorWrapperTest {

    @Test
    void testBasicBehaviour() {
        def wrapper = new IteratorWrapper(iterator: (0..12).iterator(), limit: 3, offset: 2)
        assert wrapper.hasNext()
        assert 2 == wrapper.next()
        assert wrapper.hasNext()
        assert 3 == wrapper.next()
        assert wrapper.hasNext()
        assert 4 == wrapper.next()
        assert !wrapper.hasNext()

        try {
            wrapper.next()
            assert false: "exception should have occurred"
        } catch (NoSuchElementException ex) {

        }
    }
}
