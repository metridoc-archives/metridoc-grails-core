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

import javax.xml.stream.XMLStreamReader
import metridoc.plugins.iterators.DefaultIteratorCreator
import org.junit.Test

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 9/16/11
 * Time: 9:50 AM
 * To change this template use File | Settings | File Templates.
 */
public class XlsxIteratorTest extends BaseExcelTest<XlsxIterator> {

    InputStream getDocument() {
        return getDocument("xlsx")
    }

    @Test
    void testConvertRowToHash() {
        def row = [
            new XlsxTestCell(reference: "A2", formattedValue: 5),
            new XlsxTestCell(reference: "B2", formattedValue: 10),
            new XlsxTestCell(reference: "E2", formattedValue: "foo"),
            new XlsxTestCell(reference: "G2", formattedValue: "bar")
        ]

        def columns = [
            "foo",
            "bar",
            "baz",
            "boom",
            "foobar",
            "foobaz"
        ] as LinkedHashSet

        def result = XlsxIterator.convertRowToHash(row, columns)
        assert columns.size() == result.size()
        assert result.foo == 5
        assert result.bar == 10
        assert result.baz == null
        assert result.boom == null
        assert result.foobar == "foo"
        assert result.foobaz == null
    }

    @Test
    void rowWidthIsTheMaximum() {
        def row = [
            new XlsxCell(reference: "A2"),
            new XlsxCell(reference: "B2"),
            new XlsxCell(reference: "AC2")
        ]

        assert 29 == XlsxIterator.getRowWidth(row)
    }

    @Test
    void testExtractingColumnFromRow() {

        def row = [
            new XlsxTestCell(reference: "A2", formattedValue: "foo"),
            new XlsxTestCell(reference: "B2", formattedValue: "bar"),
            new XlsxTestCell(reference: "D2", formattedValue: 5)
        ]

        def columns = XlsxIterator.getColumnsFromRowValues(row)
        testColumns(columns, ["foo", "bar", "3", "5"])
    }

    @Test
    void testGetAttributeMap() {
        def reader = [
            getAttributeLocalName: {int index ->
                switch (index) {
                    case 0: return "foo"
                    case 1: return "bar"
                    case 2: return "baz"
                }
            },
            getAttributeValue: {int index ->
                switch (index) {
                    case 0: return "baz"
                    case 1: return "bar"
                    case 2: return "foo"
                }
            },
            getAttributeCount: {
                return 3
            }
        ] as XMLStreamReader

        def map = XlsxIterator.getAttributeMap(reader)
        assert map
        assert map.get("foo") == "baz"
        assert map.get("bar") == "bar"
        assert map.get("baz") == "foo"
    }


    @Test
    void getWorkbookReaderIsNeverNull() {
        assert grid().workbookReader
    }

    @Test
    void testRetrievingSheetReferenceByIndex() {
        def grid = grid()
        assert XlsxIterator.getSheetReferenceByIndex(grid.workbookReader, 0)
        assert XlsxIterator.getSheetReferenceByIndex(grid.workbookReader, 1)
        assert !XlsxIterator.getSheetReferenceByIndex(grid.workbookReader, 100)
    }

    @Test
    void testRetrievingSheetReferenceBySheetName() {
        def grid = grid()
        assert XlsxIterator.getSheetReferenceByName(grid.workbookReader, "foo")
        assert XlsxIterator.getSheetReferenceByName(grid.workbookReader, "bar")
        assert !XlsxIterator.getSheetReferenceByName(grid.workbookReader, "blah")
    }

    @Test
    void getXssfReaderShouldNeverBeNull() {
        assert grid().xssfReader
    }

    @Test
    void getReaderShouldNeverBeNull() {
        assert grid().reader
    }

    @Test
    void getStringLookupShouldNeverBeNull() {
        assert grid().stringLookup
    }

    @Override
    DefaultIteratorCreator getCreator() {
        return new XlsxIterator()
    }

    @Override
    byte[] getGenericFileBody() {
        return getDocument("xlsx").bytes
    }

    @Override
    XlsxIterator grid() {
        new XlsxIterator().doCreate(getGenericFile(getDocument()))
    }

    @Override
    XlsxIterator grid(LinkedHashMap args) {

        return new XlsxIterator().create(getGenericFile(getDocument()), args)
    }
}

class XlsxTestCell extends XlsxCell {

    def formattedValue
}
