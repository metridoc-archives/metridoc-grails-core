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
package metridoc.camel.component.poll

import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.apache.camel.impl.DefaultCamelContext
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import metridoc.test.BaseTest

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 4/22/11
 * Time: 10:35 AM
 * To change this template use File | Settings | File Templates.
 */
class PollConsumerTest extends BaseTest {

    def endpoint
    def camelContext
    def consumerTemplate
    def int processCalled
    def int templateCalled

    @Before
    def void reset() {
        processCalled = 0
        templateCalled = 0
    }

    @Test
    def void testRemovingPropertiesFromUrl() {
        def url = "foo:bar?baz=stuff&foobar=more"

        assert "foo:bar?foobar=more" == PollConsumer.removePropertyFromUrl(url, "baz")
        assert "foo:bar?baz=stuff" == PollConsumer.removePropertyFromUrl(url, "foobar")
        assert "foo:bar?baz=stuff&foobar=more" == PollConsumer.removePropertyFromUrl(url, "foobarbaz")
        assert "foo:bar?baz=stuff&foobar=more" == PollConsumer.removePropertyFromUrl(url, null)

        url = "foo:bar?baz=stuff"
        assert "foo:bar" == PollConsumer.removePropertyFromUrl(url, "baz")

        url = "foo:bar?baz=stuff&foo=moreStuff&baz=TheMostStuff"
        assert "foo:bar?baz=stuff&baz=TheMostStuff" == PollConsumer.removePropertyFromUrl(url, "foo")
    }

    @Test
    def void testRemovingPollPrefix() {
        def nonPollUrl = "foo:bar?baz=5"
        def url = "poll:${nonPollUrl}"

        assert "foo:bar?baz=5" == PollConsumer.removePollPrefix(url)

        url = "poll://${nonPollUrl}"
        assert "foo:bar?baz=5" == PollConsumer.removePollPrefix(url)

        url = "poll://poll:foo"
        assert "poll:foo" == PollConsumer.removePollPrefix(url)

    }

    @Test
    def void consumerTemplateIsNeverNull() {

        def pollConsumer = new PollConsumer(
                endpoint: new PollEndpoint(camelContext: new DefaultCamelContext())
        )

        assert pollConsumer.consumerTemplate != null
    }


    @Test
    def void testWaitTimeDefaults() {
        def camelContext = new DefaultCamelContext()
        def PollEndpoint endpoint = camelContext.getEndpoint("poll:sftp:stuff@stuff")
        assert 0 == endpoint.wait
    }

    @Test
    def void testMaxMessages() {
        def camelContext = new DefaultCamelContext()
        def PollEndpoint endpoint = camelContext.getEndpoint("poll:sftp:stuff@stuff")
        assert 1 == endpoint.maxMessages
        endpoint = camelContext.getEndpoint("poll:sftp:stuff@stuff?maxMessages=10")
        assert 10 == endpoint.maxMessages
    }



    def Processor mockProcessor() {
        return [
                process: {Exchange exchange ->
                    processCalled++
                }
        ] as Processor
    }

    @Test(expected = IllegalArgumentException.class)
    def void errorThrownIfGettingTemplateAndEndpointIsNull() {
        new PollConsumer().consumerTemplate
    }

    @Test(expected = IllegalArgumentException.class)
    def void errorThrownIfDoStartCalledAndProcessorIsNull() {
        new PollConsumer(endpoint: getEndpoint()).doStart()
    }

    @Test(expected = IllegalArgumentException.class)
    def void errorThrownIfDoStartCalledAndEndpointIsNull() {
        def processor = [] as Processor
        new PollConsumer(processor: processor).doStart()
    }
}
