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

import java.util.zip.GZIPOutputStream
import metridoc.utils.SystemUtils
import org.apache.camel.component.file.GenericFile
import org.junit.Test

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 9/8/11
 * Time: 3:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class IteratorFactoryTest extends BaseIteratorTest {

    private testByExtension(String extension, Class expectedClass, LinkedHashMap parameters) {
        File tempFile = File.createTempFile("factory-test", "." + extension);
        tempFile.deleteOnExit();

        if (extension.endsWith(".gz")) {
            def fileOut = new FileOutputStream(tempFile)
            def gzOut = new GZIPOutputStream(fileOut)
            gzOut.write("foo,bar".bytes)
            gzOut.close()
        }

        GenericFile file = new GenericFile(body: tempFile, file: tempFile, fileNameOnly: tempFile.name)

        def resultIterator = new IteratorFactory().create(file, parameters);
        assert resultIterator.getClass() == expectedClass
        return resultIterator
    }

    @Test
    void testIteratorFactoryXls() {
        testByExtension('xls', XlsIterator.class, null);
    }

    @Test
    void testIteratorFactoryXlsx() {
        testByExtension('xlsx', XlsxIterator.class, null);
    }

    @Test
    void testIteratorFactoryCsv() {
        testByExtension('csv', CsvIterator.class, null);
    }

    @Test
    void testIteratorFactoryDefault() {
        testByExtension('na', LineIterator.class, null);
    }

    @Test
    void testIteratorFactoryXlsWithParams() {
        def xlsIterator = testByExtension('xls', XlsIterator.class, ["sheetName": "name1"]);
        assert xlsIterator.sheetName == "name1"
    }

    @Test
    void testIteratorFactoryWithGzipExtension() {
        testByExtension('csv.gz', CsvIterator.class, null)
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
