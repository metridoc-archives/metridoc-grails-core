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
package metridoc.workflows.mapper.impl

import metridoc.utils.IdUtils
import metridoc.workflows.BaseTestHelper
import org.apache.camel.Exchange
import org.apache.camel.Message
import org.junit.Test
import static metridoc.workflows.mapper.impl.BasicListToMapTransformer.PARSER_MUST_BE_SET

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 2/24/12
 * Time: 12:26 PM
 */
class BasicListToMapTransformerTest extends BaseTestHelper {

    def newBody
    def result = [foo: "bar", bar: 5]
    def keys = [foo_id: ["foo", "bar"]]
    def expectedKey = IdUtils.md5(BasicListToMapTransformer.DEFAULT_KEY_DELIMITER, "bar", "5")
    boolean getHeadersCalled = false
    boolean setHeadersCalled = false


    @Test
    void parserMustBeSet() {
        testForAssertionError(PARSER_MUST_BE_SET) {
            transformer(null).transform([] as Exchange)
        }
    }

    @Test
    void parseListUsingOnlyIndexes() {
        transformer([foo: 0]).transform(exchange(["bar"]))
        assert getHeadersCalled
        assert setHeadersCalled
        assert newBody == [foo: "bar"]
    }

    @Test
    void parseAListWithClosure() {
        transformer([foo: {Exchange message -> message.in.body.get(0)}]).transform(exchange(["bar"]))
        assert newBody == [foo: "bar"]
    }

    @Test
    void testCalculatingKeys() {
        assert expectedKey == new BasicListToMapTransformer().getKey(["bar", 5])
    }

    @Test
    void testCalculatingKeysWithResultAndKeyList() {
        new BasicListToMapTransformer().addKeys(keys, result)
        expectedKey == result.foo_id
    }

    @Test
    void testDataRetrieval() {
        def keyArray = ["foo", "bar"]

        assert ["bar", 5] == new BasicListToMapTransformer().getData(keyArray, result)
    }


    Message message(List body) {
        [
            getBody: {Class<List> clazz ->
                body
            },
            getBody: {
                body
            },
            setBody: {
                newBody = it
            },
            getHeaders: {
                getHeadersCalled = true
                return [:]
            },
            setHeaders: {Map headers ->
                setHeadersCalled = true
            }
        ] as Message
    }

    Exchange exchange(List body) {
        def message = message(body)
        [
            getIn: {
                message
            },
            getOut: {
                message
            }
        ] as Exchange
    }

    BasicListToMapTransformer transformer(Map parser) {
        new BasicListToMapTransformer(parser: parser)
    }
}
