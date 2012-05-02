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

import org.junit.Test

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 11/28/11
 * Time: 3:41 PM
 */
public class CsvIteratorTest {

    @Test
    void testDefaultCsvBehaviour() {
        def parser = simpleCsvGrid().csvReader
        def line = parser.readNext()
        assert 2 == line.size()
        assert "foo" == line[0]
        assert "bar" == line[1]

        line = parser.readNext()
        assert 2 == line.size()
        assert "bam" == line[0]
        assert "boom" == line[1]

        assert null == parser.readNext()
    }

    @Test
    void testNextAndHasNextRelationship() {
        def grid = simpleCsvGrid()

        assert grid.hasNext()
        grid.next()
        assert grid.hasNext()
        grid.next()
        assert !grid.hasNext()
    }

    @Test
    void testBasicNextWithoutHeader() {
        def grid = simpleCsvGrid()
        assert grid.hasNext()
        def next = grid.next()
        assert "foo" == next[0]
        assert "bar" == next[1]

        assert grid.hasNext()
        next = grid.next()
        assert "bam" == next[0]
        assert "boom" == next[1]

        assert !grid.hasNext()

        try {
            grid.next()
            assert false: "exception should have occurred"
        } catch (NoSuchElementException ex) {

        }
    }

    @Test
    void testThatTheEscapeCharacterIsCorrect() {
        char defaultEscape = '\\'
        assert defaultEscape == simpleCsvGrid().csvReader.parser.escape
    }

    InputStream getStream(String text) {
        return new ByteArrayInputStream(text.bytes)
    }

    CsvIterator simpleCsvGrid(String csv) {
        def input = getStream(csv)
        new CsvIterator().doCreate(input)
    }

    CsvIterator simpleCsvGrid() {
        def csv = "foo,bar\nbam,boom"
        simpleCsvGrid(csv)
    }
}
