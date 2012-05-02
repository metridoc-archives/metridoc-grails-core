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

import metridoc.camel.iterator.CloseableFileIterator

import org.apache.camel.Exchange
import metridoc.camel.iterator.IteratorCreator

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 7/21/11
 * Time: 12:17 PM
 *
 * a helper class to wrap our iterator api around a collection.  In this case fileName will probably be null most of
 * the time.  One could use it as an identifier.  This class is frequently used for testing so the file system does
 * not have to be accessed
 *
 * We have migrated all iterator operations to the iterator-plugin module
 *
 * @deprecated
 */
class CollectionsIterator implements CloseableFileIterator, IteratorCreator {

    String fileName //can put in a fake one
    Collection collection = []
    Iterator iterator

    Iterator getIterator() {
        if(iterator != null) {
            return iterator
        }

        iterator = collection.iterator()
    }

    void close() {
        //do nothing
    }

    boolean hasNext() {
        return getIterator().hasNext()
    }

    Object next() {
        return getIterator().next()
    }

    void remove() {
        getIterator().remove()
    }

    Iterator create(Object object) {
        if(object instanceof Iterator) {
            return (Iterator) object
        }

        if(object instanceof Iterable) {
            return ((Iterable) object).iterator()
        }



        if (object instanceof Exchange) {
            Exchange exchange = (Exchange) object
            def collection = exchange.in.getBody(Collection.class)
            if(collection) {
                return new CollectionsIterator(collection: collection)
            }

        }

        throw new IllegalArgumentException("Could not convert " + object + " into an iterator")
    }
}
