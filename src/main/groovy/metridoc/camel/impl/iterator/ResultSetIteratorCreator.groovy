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

import metridoc.camel.iterator.IteratorCreator
import groovy.sql.GroovyRowResult
import org.apache.camel.Exchange
import java.sql.ResultSet
import org.slf4j.LoggerFactory
import metridoc.utils.ColumnConstrainedMap

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 8/4/11
 * Time: 10:42 AM
 *
 * Iterates through a {@link ResultSet} returning a ${@link GroovyRowResult} for each next.
 *
 * @deprecated
 *
 */
class ResultSetIteratorCreator implements IteratorCreator<GroovyRowResult>, Iterator<GroovyRowResult>, Closeable {

    /**
     * the ${@link ResultSet} to iterate over
     */
    ResultSet resultSet
    Set<String> columns

    def static final log = LoggerFactory.getLogger(ResultSetIteratorCreator)
    /**
     * The next result
     */
    GroovyRowResult next
    /**
     * Indicates whether the ${@link Iterator} has been closed (ie the {@link ResultSet}.  If it has
     * ${@link ResultSetIteratorCreator#hasNext} should always return <code>null</code>
     */
    boolean closed = false

    /**
     * creates a ${@link ResultSetIteratorCreator} based on the object.  Must be able to extract a ${@link ResultSet}
     * from the passed Object.  Basically the object can be a {@link ResultSet} or an {@link Exchange} which has a
     * body that is a {@link ResultSet}
     *
     *
     * @param object
     * @return
     */
    Iterator<GroovyRowResult> create(Object object) {

        ResultSet resultSetToIterate
        if(object instanceof Exchange) {
            def exchange = object as Exchange
            resultSetToIterate = exchange.in.getBody(ResultSet.class)
        }

        if(object instanceof ResultSet) {
            resultSetToIterate = object as ResultSet
        }

        if(resultSetToIterate) {
            return new ResultSetIteratorCreator(resultSet: resultSetToIterate)
        }

        throw new IllegalArgumentException("Could not extract a ResultSet from ${object}")
    }

    void close() {
        try {
            resultSet.close()
            closed = true
        } catch (Exception e) {
            log.warn("ignoring an exception that occurred when trying to close result set", e)
        }
    }

    boolean hasNext() {
        if(closed) {
            return false
        }

        if(next) {
            return true
        }

        def nextResultExists = resultSet.next()
        if(nextResultExists) {
            next = resultSet.toRowResult()
        }
        
        return nextResultExists
    }

    GroovyRowResult next() {
        if(!hasNext()) {
            throw new NoSuchElementException("No more elements exist")
        }
        def notColumnConstrainedResult = next
        next = null

        def columnConstrainedResult = notColumnConstrainedResult  //default case if not constrained by columns
        if(columns) {
            columnConstrainedResult = new GroovyRowResult(new ColumnConstrainedMap(notColumnConstrainedResult, columns))
        }

        return columnConstrainedResult
    }

    void remove() {
        throw new UnsupportedOperationException("remove is not suportted in ${ResultSetIteratorCreator.class.name}")
    }
}
