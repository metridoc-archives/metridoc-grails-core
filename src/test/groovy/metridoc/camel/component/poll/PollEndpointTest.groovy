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

import metridoc.utils.Assert
import org.apache.camel.Processor
import org.junit.Test
import metridoc.test.BaseTest

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 4/24/11
 * Time: 3:27 PM
 * To change this template use File | Settings | File Templates.
 */
class PollEndpointTest extends BaseTest {

    @Test(expected = UnsupportedOperationException.class)
    def void creatingAProducerCausesAnError() {
        new PollEndpoint().createProducer()
    }

    @Test
    def void PollingEndpointIsASingleton() {
        assert new PollEndpoint().singleton
    }

    @Test(expected = IllegalArgumentException.class)
    def void throwsErrorIfCamelContextNotSetWhenCreatingConsumer() {
        def processor = [] as Processor
        new PollEndpoint().createConsumer processor
    }
}
