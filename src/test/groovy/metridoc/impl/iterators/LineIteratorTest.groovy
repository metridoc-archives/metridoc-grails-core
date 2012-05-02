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
package metridoc.plugins.impl.iterators

import metridoc.plugins.PluginDB
import metridoc.utils.SystemUtils
import org.junit.Test

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 9/8/11
 * Time: 3:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class LineIteratorTest extends BaseIteratorTest {

    @Test
    void LineGridIsAPlugin() {
        def db = PluginDB.instance
        def plugins = db.getPlugins("grid")
        def lineGridPlugin

        plugins.each {
            if (it == LineIterator.class) {
                lineGridPlugin = it
            }
        }

        assert lineGridPlugin
    }

    @Test
    void testBasicIterationOperations() {
        def lineGrid = new LineIterator().doCreate(getGenericFile())

        assert lineGrid.hasNext()
        assert "foo" == lineGrid.next()[0]

        assert lineGrid.hasNext()
        assert "bar" == lineGrid.next()[0]

        assert lineGrid.hasNext()
        assert "baz" == lineGrid.next()[0]

        assert !lineGrid.hasNext()
    }

    @Override
    LineIterator getCreator() {
        return new LineIterator()
    }

    @Override
    byte[] getGenericFileBody() {
        def text = "foo${SystemUtils.LINE_SEPARATOR}bar${SystemUtils.LINE_SEPARATOR}baz"
        def stream = new ByteArrayInputStream(text.getBytes())

        return stream.bytes
    }

}
