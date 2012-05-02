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
package metridoc.utils

import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 9/27/11
 * Time: 4:07 PM
 */
public class IdUtilsTest {

    @Test
    void ifDataOnlyHasOneValueConcatReturnsIt() {
        assert "bar" == IdUtils.concat("|", "bar")
    }

    @Test
    void testLongConversion() {
        long foo = 1000L
        def bytes = IdUtils.convertLong(foo)
        assert 8 == bytes.size()
    }

    @Test
    void testConvertAndConcatenate() {
        long foo = 1000L
        long bar = 123123L
        def bytes = IdUtils.convertAndCombineLongs(foo, bar)

        assert 16 == bytes.size()
    }

    @Test
    void testByteSizeOfUUID() {
        assert 16 == IdUtils.uuid().length
    }

    @Test
    void testBasicUniquenessOfUUID() {
        assert IdUtils.uuid() != IdUtils.uuid()
    }

    @Test
    void testByteSizeOfMD5() {
        String data = "foo"
        assert 16 == IdUtils.md5(data).length
    }

    @Test
    void testConcatenation() {
        assert "foo_bar_" == IdUtils.concat("_", "foo", "bar")
    }

    @Test
    void testConcatenatedMd5() {
        assert 16 == IdUtils.md5("_", "foo", "bar").length
    }
}
