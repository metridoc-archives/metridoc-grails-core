/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.upennlib.metridoc.impl.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.NoSuchElementException;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author tbarker
 */
public class ExcelIteratorCreatorTest extends CamelTestSupport {

    private ExcelIteratorCreator excelIteratorCreator;
    private InputStream inputStream;

    @Before
    public void setFile() throws FileNotFoundException {
        inputStream = new FileInputStream(new File("src/test/resources/testFiles/excel/smallTestFile.xls"));
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

    @Test
    public void supportsExtensionXls() {
        assertTrue(excelIteratorCreator.supportsExtension(".xls"));
        assertTrue(excelIteratorCreator.supportsExtension("xls"));
        assertFalse(excelIteratorCreator.supportsExtension("txt"));
    }

}