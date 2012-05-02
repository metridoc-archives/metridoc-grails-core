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
package metridoc.camel.impl.iterator

import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 9/4/11
 * Time: 4:34 PM
 */
public class BatchIteratorTest {

    @Test(expected=IllegalArgumentException.class)
    void wrappedIteratorCannotBeNull() {
        new BatchIterator(null, 5)
    }

    @Test(expected=IllegalArgumentException.class)
    void batchSizeCannotBeLessThan0() {
        new BatchIterator([] as Iterator, -1)
    }

    @Test
    void testBasicBatching() {
        def numbers = [1,2,3,4,5]
        def iterator = new BatchIterator(numbers.iterator(), 2)
        assert iterator.hasNext()

        def batch = iterator.next()
        assert batch.size() == 2
        assert batch.get(0) == 1
        assert batch.get(1) == 2

        batch = iterator.next()
        assert batch.size() == 2
        assert batch.get(0) == 3
        assert batch.get(1) == 4

        batch = iterator.next()
        assert batch.size() == 1
        assert batch.get(0) == 5

        assert !iterator.hasNext()

        try {
            iterator.next()
            assert false : "exception should have occurred"
        } catch (NoSuchElementException ex) {

        }
    }
}
