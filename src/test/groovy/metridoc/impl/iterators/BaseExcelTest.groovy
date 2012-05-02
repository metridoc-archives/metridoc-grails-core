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

import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.junit.Test

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 9/16/11
 * Time: 10:29 AM
 * To change this template use File | Settings | File Templates.
 */
abstract class BaseExcelTest<T extends BaseExcelIterator> extends BaseIteratorTest {

    abstract T grid()
    abstract T grid(LinkedHashMap args)

    @Test
    void testDefaultFunctionality() {
        def grid = grid()
        assert grid.hasNext()
        def next = grid.next()
        assert next[0] == "foo"
        assert next[1] == 5

        try {
            grid.next()
            assert false: "exception should have occurred"
        } catch (NoSuchElementException ex) {

        }

    }

    @Test
    void testSparseSpreadSheet() {
        def grid = grid(sheetName: "bar")
        assert grid.hasNext()

        def next = grid.next()

        assert 3 == next.size()
        assert next[0] == "foo"
        assert next[1] == null
        assert next[2] == "baz"

        next = grid.next()

        assert 4 == next.size()
        assert next[0] == 0
        assert next[1] == 1
        assert next[2] == null
        assert next[3] == 3

        next = grid.next()

        assert 2 == next.size()
        assert next[0] == 3
        assert next[1] == 5

        assert !grid.hasNext()
    }

    static InputStream getDocument(String type) {
        Workbook wb
        switch(type) {
            case "xls" :
                wb = new HSSFWorkbook()
                break;
            case "xlsx":
                wb = new XSSFWorkbook()
        }

        def output = new ByteArrayOutputStream()
        Sheet sheet = wb.createSheet("foo")
        createRow(sheet, 0, ["foo", 5])

        sheet = wb.createSheet("bar")
        createRow(sheet, 0, ["foo", null, "baz", null])
        createRow(sheet, 1, [0, 1, null, 3])
        createRow(sheet, 2, [3, 5])

//        in case you want to output the file
//        def home = System.getProperty("user.home")
//        def fileOutput = new FileOutputStream(new File("${home}/foo.${type}"))
//        wb.write(fileOutput)

        wb.write(output)
        return new ByteArrayInputStream(output.toByteArray())
    }

    void testColumns(LinkedHashSet<String> columns, List expected) {
        assert columns.size() == expected.size()
        def columnList = columns as List
        for (int i = 0; i < columns.size(); i++) {
            assert columnList.get(i) == String.valueOf(expected.get(i))
        }
    }

    static Row createRow(Sheet sheet, int rowIndex, List values) {
        Row row = sheet.createRow(rowIndex)
        for (int i = 0; i < values.size(); i++) {
            def value = values.get(i)
            if (value != null) {
                row.createCell(i).setCellValue(values.get(i))
            }
        }

        return row
    }

    Row createRow(List values) {
        Workbook wb = new HSSFWorkbook()
        Sheet sheet = wb.createSheet("foo")
        return createRow(sheet, 0, values)
    }

    Cell createCell(Object value) {
        def row = createRow([value])
        return row.getCell(0)
    }

}
