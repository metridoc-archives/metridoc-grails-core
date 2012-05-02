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
package metridoc.camel.iterator;

import metridoc.utils.IOUtils;
import org.apache.camel.Exchange;
import org.apache.camel.component.file.GenericFile;

import java.io.File;
import java.io.InputStream;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 7/21/11
 * Time: 10:36 AM
 */
public abstract class DefaultFileIterator<T> implements IteratorCreator<T>, CloseableFileIterator<T> {

    private String fileName;

    @Override
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public Iterator<T> create(Object object) throws Exception{

        InputStream inputStream = null;
        File file = null;
        if (object instanceof Exchange) {
            Exchange exchange = (Exchange) object;
            inputStream = IOUtils.convertGenericFileToInputStream(exchange);
            file = IOUtils.getFile(exchange);

        }

        if(object instanceof GenericFile) {
            GenericFile genericFile = (GenericFile) object;
            inputStream = IOUtils.convertGenericFileToInputStream(genericFile);
            file = IOUtils.getFile(genericFile);
        }

        if (object instanceof File) {
            file = (File) object;
            inputStream = IOUtils.convertFileToInputStream(file);
        }

        if (file != null) {
            fileName = file.getName();
        }

        if(object instanceof InputStream) {
            inputStream = (InputStream) object;
        }

        if(inputStream == null) {
            throw new IllegalArgumentException("The argument " + object.toString() +
                    " could not be converted to an InputStream");
        }

        return doCreate(inputStream);
    }

    public abstract CloseableFileIterator<T> doCreate(InputStream inputStream);
}
