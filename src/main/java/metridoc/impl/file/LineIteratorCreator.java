/**
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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package metridoc.impl.file;

import metridoc.file.IteratorCreator;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import org.apache.commons.io.LineIterator;

/**
 *
 * @author tbarker
 */
public class LineIteratorCreator implements IteratorCreator<String>{

    public static final String BEAN_NAME = "lineIteratorCreator";

    @Override
    public Iterator<String> create(InputStream stream) {
        return new LineIterator(new InputStreamReader(stream));
    }

    @Override
    public boolean supportsExtension(String extension) {
        return
                "txt".equalsIgnoreCase(extension) || ".txt".equalsIgnoreCase(extension) ||
                "csv".equalsIgnoreCase(extension) || ".csv".equalsIgnoreCase(extension);
    }

    @Override
    public String getName() {
        return DefaultIteratorNames.TXT.getDescription();
    }

}
