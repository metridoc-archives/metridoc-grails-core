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
import metridoc.workflows.mapper.MessageTransformer
import org.apache.camel.Exchange
import groovy.util.logging.Slf4j

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 2/24/12
 * Time: 12:25 PM
 */
@Slf4j
class BasicListToMapTransformer implements MessageTransformer<Exchange> {
    public static final String PARSER_MUST_BE_SET = "parser must not be null"
    public static final String DEFAULT_KEY_DELIMITER = "#|#"
    String keyDelimiter = DEFAULT_KEY_DELIMITER

    Map<String, Object> parser

    void transform(Exchange message) {
        try {
            assert parser: PARSER_MUST_BE_SET

            def body = message.in.getBody(List)
            def result = [:]
            def keys = [:]
            parser.each {key, value ->
                if (value instanceof Integer) {
                    result[key] = body.get(value)
                } else if (value instanceof Closure) {
                    result[key] = value(message)
                } else if (value instanceof List) {
                    keys[key] = value
                }
            }

            addKeys(keys, result)
            message.out.body = result
            message.out.headers = message.in.headers
        } catch (Exception e) {
            log.error("Exception occurred tryng to transform message with body {}", message.in.body)
            throw e
        }
    }

    protected void addKeys(Map keys, Map result) {
        keys.each {String keyColumn, List keyArray ->
            def dataToCreateKeyWith = getData(keyArray, result)
            result[keyColumn] = getKey(dataToCreateKeyWith)
        }
    }

    protected List getData(List keyArray, Map result) {
        def data = []
        keyArray.each {String key ->
            if (!result.containsKey(key)) {
                assert result.containsKey(key): "the key ${key} cannot be found in ${result}"
            }
            data.add(result[key])
        }

        return data
    }

    protected byte[] getKey(List data) {
        def textData = []
        data.each {
            textData.add(it as String)
        }
        IdUtils.md5(keyDelimiter, textData as String[])
    }
}
