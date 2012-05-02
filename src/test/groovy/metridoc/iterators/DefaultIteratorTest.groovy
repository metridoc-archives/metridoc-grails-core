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
package metridoc.plugins.iterators

import org.junit.Test

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 9/20/11
 * Time: 4:19 PM
 */
public class DefaultIteratorTest {

    //TODO: refactor these tests, repeating a lot of stuff here

    @Test
    void testCloseIsCalledWhenHashNextIsFalse() {
        def foo = new FooIterator()
        assert foo.hasNext()
        assert !foo.closeCalled
        assert foo.next()
        assert !foo.closeCalled
        assert foo.hasNext()
        assert !foo.closeCalled
        assert foo.next()
        assert !foo.closeCalled
        assert !foo.hasNext()
        assert foo.closeCalled
    }

    @Test
    void testNextWhenThereAreNullValuesAndRecordsVaryInSize() {
        def content = [
            ["2", null, "bar"],
            ["foobar", "barfoo"]
        ].iterator()
        def grid = new FooIterator(content: content)
        def record = grid.next()
        assert null == record[1]
        record = grid.next()
        assert 2 == record.size()
    }
}

class FooIterator extends DefaultIterator implements Closeable {

    boolean closeCalled = false
    def content = [["foo", "bar"], ["foobar", "barbar"]].iterator()

    void remove() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    List doNext() {
        if (content.hasNext()) {
            return content.next()
        }

        return null
    }

    void close() {
        closeCalled = true
    }

}