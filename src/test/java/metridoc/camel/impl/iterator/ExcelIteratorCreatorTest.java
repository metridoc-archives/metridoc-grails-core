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

package metridoc.camel.impl.iterator;

import metridoc.utils.IOUtils;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.NoSuchElementException;

/**
 *
 * @author tbarker
 */
public class ExcelIteratorCreatorTest extends CamelTestSupport {

    private ExcelIteratorCreator excelIteratorCreator;
    private InputStream inputStream;

    @Before
    public void setFile() throws FileNotFoundException {
        File file = new File("src/test/resources/testFiles/metridoc/camel/impl/iterator/excel/smallTestFile.xls");

        if(!file.exists()) {
            //probably running from trunk then
            file = new File("plugins/metridoc-camel/metridoc-camel-core/src/test/resources/testFiles/metridoc/camel/impl/iterator/excel/smallTestFile.xls");
        }

        assertTrue(file.exists());

        inputStream = new FileInputStream(file);
        excelIteratorCreator = new ExcelIteratorCreator(inputStream);
    }

    @After
    public void closeFile() {
        IOUtils.closeQuietly(inputStream);
    }

    @Test
    public void usingConverterExcelIteratorReturnsStringArrayOfValues() {
        String[] values = context.getTypeConverter().convertTo(String[].class, excelIteratorCreator.next());
        assertEquals(3, values.length);
        assertEquals("foo", values[0]);
        assertEquals("foobar", values[1]);
        assertEquals("foobaz", values[2]);

        values = context.getTypeConverter().convertTo(String[].class, excelIteratorCreator.next());
        assertEquals(3, values.length);
        assertEquals("barA2", values[0]);
        assertEquals("barB2", values[1]);
        assertEquals("1", values[2]);
    }

    @Test
    public void aFileWithThreeRowsFailsOnFourthNext() {
        excelIteratorCreator.next();
        excelIteratorCreator.next();
        excelIteratorCreator.next();

        try {
            excelIteratorCreator.next();
            fail("exception should have occurred");
        } catch (NoSuchElementException e) {
        }
    }
}