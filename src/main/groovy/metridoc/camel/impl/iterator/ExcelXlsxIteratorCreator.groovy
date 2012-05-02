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
package metridoc.camel.impl.iterator

import org.apache.poi.xssf.usermodel.XSSFRow

import metridoc.camel.iterator.CloseableFileIterator

import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import metridoc.camel.iterator.DefaultFileIterator

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 7/9/11
 * Time: 7:50 PM
 *
 * We have migrated all iterator operations to the iterator-plugin module
 *
 * @deprecated
 */
class ExcelXlsxIteratorCreator extends DefaultFileIterator<XSSFRow> {

    InputStream inputStream
    XSSFSheet sheet;
    private int nextZeroBasedRow = 0;

    private void initializeSheetIfNull() {

        if (sheet == null) {
            try {
                sheet = new XSSFWorkbook(inputStream).getSheetAt(0);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    void close() {
        metridoc.utils.IOUtils.closeQuietly(inputStream);
    }

    boolean hasNext() {
        initializeSheetIfNull()

        return sheet.getRow(nextZeroBasedRow) != null;
    }

    XSSFRow next() {
        initializeSheetIfNull();
        XSSFRow row = sheet.getRow(nextZeroBasedRow);

        if (row == null) {
            throw new NoSuchElementException("No more rows left");
        }

        nextZeroBasedRow++;
        return row;
    }

    void remove() {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    CloseableFileIterator<XSSFRow> doCreate(InputStream inputStream) {
        return new ExcelXlsxIteratorCreator(inputStream: inputStream);
    }
}
