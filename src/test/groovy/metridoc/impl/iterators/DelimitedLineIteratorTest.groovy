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

public class DelimitedLineIteratorTest extends BaseIteratorTest {

    private static final String DELIMITER_REGEX = /\*\*/
    private static final String DELIMITER = "**"

    @Test
    void DelimitedLineGridIsAPlugin() {
        def db = PluginDB.instance
        def plugins = db.getPlugins("grid")
        def delimitedLineGridPlugin

        plugins.each {
            if (it == DelimitedLineIterator.class) {
                delimitedLineGridPlugin = it
            }
        }

        assert delimitedLineGridPlugin
    }

    @Test
    void testBasicIterationOperations() {
        def delimitedLineGrid = new DelimitedLineIterator().create(getGenericFile(), [delimiter: DELIMITER_REGEX])

        assert delimitedLineGrid.hasNext()
        assert ["foo", "foo1", "foo2"] == delimitedLineGrid.next()

        assert delimitedLineGrid.hasNext()
        assert ["bar", "bar1", "bar2"] == delimitedLineGrid.next()

        assert delimitedLineGrid.hasNext()
        assert ["baz", "baz1"] == delimitedLineGrid.next()

        assert !delimitedLineGrid.hasNext()
    }

    @Override
    DelimitedLineIterator getCreator() {
        return new DelimitedLineIterator()
    }

    @Override
    byte[] getGenericFileBody() {
        def text = "foo${DELIMITER}foo1${DELIMITER}foo2${SystemUtils.LINE_SEPARATOR}"
        text += "bar${DELIMITER}bar1${DELIMITER}bar2${DELIMITER}${SystemUtils.LINE_SEPARATOR}baz${DELIMITER}baz1"
        def stream = new ByteArrayInputStream(text.getBytes())

        return stream.bytes
    }
}
