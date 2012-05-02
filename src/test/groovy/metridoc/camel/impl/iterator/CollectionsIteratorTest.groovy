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

import org.junit.Test
import org.apache.camel.Message
import org.apache.camel.Exchange
import metridoc.test.BaseTest;

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 7/21/11
 * Time: 12:55 PM
 */
public class CollectionsIteratorTest extends BaseTest{

    @Test
    void testBasicFunctionality() {
        def collection = ["foo", "bar"]

        def iterator = new CollectionsIterator().create(
                [
                    getIn: {
                        [getBody: {Class clazz ->
                            collection
                        }
                        ] as Message
                    }
                ] as Exchange
        )

        assert "foo" == iterator.next()
        assert "bar" == iterator.next()

        assert iterator.hasNext() == false
    }

}
