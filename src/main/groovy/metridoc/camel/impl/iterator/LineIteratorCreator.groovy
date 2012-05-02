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

import metridoc.camel.iterator.CloseableFileIterator;
import metridoc.camel.iterator.DefaultFileIterator;
import metridoc.utils.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 *
 * @author tbarker
 */
public class LineIteratorCreator extends DefaultFileIterator<String> {

    private LineIterator lineIterator;

    public LineIteratorCreator() {}

    public LineIteratorCreator(InputStream stream) {
        Assert.notNull(stream, "stream cannot be null");
        lineIterator = new LineIterator(new InputStreamReader(stream));
    }

    @Override
    public CloseableFileIterator<String> doCreate(InputStream inputStream) {
        return new LineIteratorCreator(inputStream);
    }

    @Override
    public void close() throws IOException {
        lineIterator.close();
    }

    @Override
    public boolean hasNext() {
        return lineIterator.hasNext();
    }

    @Override
    public String next() {
        return lineIterator.nextLine();
    }

    @Override
    public void remove() {
        lineIterator.remove();
    }
}
