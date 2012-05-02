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

import org.apache.camel.Exchange
import metridoc.camel.iterator.IteratorCreator
import org.apache.camel.component.file.GenericFile
import metridoc.utils.IOUtils

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 7/24/11
 * Time: 11:33 AM
 *
 * We have migrated all iterator operations to the iterator-plugin module
 *
 * @deprecated
 */
class IteratorProviderFactory {

    private static Map<String, IteratorCreator> iteratorProviders = [:];

    static {
        iteratorProviders["xls"] = new ExcelIteratorCreator()
        iteratorProviders["xlsx"] = new ExcelXlsxIteratorCreator()
        iteratorProviders["xls.gz"] = new ExcelIteratorCreator()
        iteratorProviders["xlsx.gz"] = new ExcelXlsxIteratorCreator()
        iteratorProviders["txt"] = new LineIteratorCreator()
        iteratorProviders["log"] = new LineIteratorCreator()
        iteratorProviders["log.gz"] = new LineIteratorCreator()
        iteratorProviders["txt.gz"] = new LineIteratorCreator()
    }

    static void addIterator(String description, IteratorCreator creator) {
        iteratorProviders[description] = creator
    }

    static Iterator getIterator(String description) {
        if(!iteratorProviders.containsKey(description)) {
            throw IteratorNotFoundException.byDescription(description)
        }

        return iteratorProviders[description]
    }


    static Iterator create(File file) {
        def fileName = file.name
        iteratorProviders.each {key, value ->
            if(fileName.endsWith(key)) {
                return iteratorProviders[key]
            }
        }

        throw IteratorNotFoundException.byFile(file)
    }

    static Iterator create(Exchange exchange) {
        def body = exchange.in.body

        if(body && body instanceof GenericFile) {
            def file = (GenericFile) body
            return create(IOUtils.getFile(file))
        }

        if(body && body instanceof File) {
            def file = (File) body
            return create(file)
        }

        if(body && body instanceof Iterator) {
            return body
        }

        if(body && body instanceof Iterable) {
            def iterable = (Iterable) body
            return iterable.iterator()
        }

        throw IteratorNotFoundException.byExchange(exchange)
    }
}

/**
 * We have migrated all iterator operations to the iterator-plugin module
 *
 * @deprecated
 */
class IteratorNotFoundException extends RuntimeException {

    IteratorNotFoundException(String s) {
        super(s)
    }

    static IteratorNotFoundException byDescription(String description) {
        return new IteratorNotFoundException("Could not find iterator with description ${description}")
    }

    static IteratorNotFoundException byExchange(Exchange exchange) {
        return new IteratorNotFoundException("could not find iterator for exchange ${exchange}")
    }

    static IteratorNotFoundException byFile(File file) {
        return new IteratorNotFoundException("Could not file Iterator for file ${file}")
    }
}

