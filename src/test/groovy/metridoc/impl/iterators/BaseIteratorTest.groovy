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

import metridoc.plugins.iterators.IteratorCreator
import org.apache.camel.component.file.GenericFile
import org.apache.camel.test.junit4.CamelTestSupport
import org.junit.Test

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 9/15/11
 * Time: 11:21 AM
 * To change this template use File | Settings | File Templates.
 */
abstract class BaseIteratorTest extends CamelTestSupport {

    boolean runRemoveTest = true

    @Test
    void testThatANewInstanceIsCreatedEachTime() {
        def creator = getCreator()
        def iteratorA = creator.create(getGenericFile(), [:])
        def iteratorB = creator.create(getGenericFile(), [:])

        assert iteratorA != iteratorB
    }

    @Test
    void testThatRemoveThrowsError() {
        if (runRemoveTest) {
            try {
                getCreator().create(getGenericFile(), [:]).remove()
                assert false: "exception should have occurred"
            } catch (UnsupportedOperationException ex) {
            }
        }
    }

    GenericFile getGenericFile(InputStream inputStream) {
        return getGenericFile(inputStream.bytes)
    }

    GenericFile getGenericFile(byte[] body) {
        File file = File.createTempFile("test", null)
        file.deleteOnExit()
        new FileOutputStream(file).write(body)

        return new GenericFile(file: file, body: file, fileNameOnly: file.name)
    }

    GenericFile getGenericFile() {
        getGenericFile(getGenericFileBody())
    }

    abstract IteratorCreator getCreator()

    abstract byte[] getGenericFileBody()
}
