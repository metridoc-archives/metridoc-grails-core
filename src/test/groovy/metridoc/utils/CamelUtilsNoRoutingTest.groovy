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

import org.junit.Test

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 9/1/11
 * Time: 12:30 PM
 *
 * These wtests don't require a Camel routing engine.  Setting up a camel routing engine takes awhile, just wanted to
 * isolate the fast tests from the ones that take awhile to run to make development faster
 *
 */
class CamelUtilsNoRoutingTest {

    @Test
    void pollingConversionAddsPollPrefixByDefault() {
        def uri = "foo:bar"
        assert "poll:foo:bar" == CamelUtils.convertToPollingEndpoint(uri)
    }

    @Test
    void pollingConversionNotDoneByDefaultWithDirectOrSeda() {
        def uri = "seda:bar"
        assert "seda:bar" == CamelUtils.convertToPollingEndpoint(uri)
        uri = "direct:bar"
        assert "direct:bar" == CamelUtils.convertToPollingEndpoint(uri)
    }

    @Test(expected=IllegalArgumentException.class)
    void pollingConversionThrowsIllegalArgumentExceptionIfTrueAndIsDirect() {
        def uri = "direct:bar?usePolling=true"
        CamelUtils.convertToPollingEndpoint(uri)
    }

    @Test
    void pollingConversionNotDoneIfUsePollingSetToFalse() {
        def uri = "foo:bar?usePolling=false"
        assert "foo:bar" == CamelUtils.convertToPollingEndpoint(uri)
    }

    @Test
    void testConversionWithMultipleParametersAndUsePollingSetToFalse() {
        def uri = "foo:bar?usePolling=false&fooBar=false"
        assert "foo:bar?fooBar=false" == CamelUtils.convertToPollingEndpoint(uri)
        uri = "foo:bar?baz=false&usePolling=false&fooBar=false"
        assert "foo:bar?baz=false&fooBar=false" == CamelUtils.convertToPollingEndpoint(uri)
        uri = "foo:bar?baz=false&fooBar=false&usePolling=false"
        assert "foo:bar?baz=false&fooBar=false" == CamelUtils.convertToPollingEndpoint(uri)
    }


}
