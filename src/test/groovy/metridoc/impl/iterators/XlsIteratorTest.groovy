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
 * Date: 9/15/11
 * Time: 3:39 PM
 */
public class XlsIteratorTest extends BaseExcelTest<XlsIterator> {

    @Test
    void testSheetInitialization() {
        def grid = grid()
        grid.initializeSheetIfNull()
        assert grid.sheet
    }

    @Test
    void testGetCellValue() {
        def cell = createCell("foo")
        assert "foo" == XlsIterator.getCellValue(cell)
    }

    XlsIterator grid() {
        return new XlsIterator().doCreate(getGenericFile(getDocument()))
    }

    XlsIterator grid(LinkedHashMap args) {
        return new XlsIterator().create(getGenericFile(getDocument()), args)
    }

    InputStream getDocument() {
        getDocument("xls")
    }

    @Override
    XlsIterator getCreator() {
        return new XlsIterator()
    }

    @Override
    byte[] getGenericFileBody() {
        return getDocument("xls").bytes
    }


}
