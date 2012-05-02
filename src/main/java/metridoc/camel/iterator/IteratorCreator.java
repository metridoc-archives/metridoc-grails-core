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

import org.apache.camel.Exchange;

import java.io.Closeable;
import java.io.File;
import java.io.InputStream;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 7/25/11
 * Time: 10:09 PM
 */
public interface IteratorCreator<T> extends Closeable{


    /**
     * creates an iterator with the input.  If it cannot, it throws an IllegalArgumentException
     * @param o
     * @return
     */
    Iterator<T> create(Object o) throws Exception;
}
