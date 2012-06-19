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

import metridoc.dsl.JobBuilder
import metridoc.test.BaseTest
import org.apache.camel.Exchange
import org.junit.Before
import org.junit.Test

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 4/13/11
 * Time: 11:35 AM
 * To change this template use File | Settings | File Templates.
 */
class OnePollOnlyStrategyJobTest extends BaseTest {

    private String baseDir

    @Before
    def void setup() {
        baseDir = "target/test-classes/testFiles/metridoc/camel/component/poll/onePollOnlyTest"

        if (!new File("${baseDir}/file1").exists()) {
            baseDir = "metridoc-core/${baseDir}"
        }
    }

    @Test
    def void testPolling() {
        assert new File("${baseDir}/file1").exists()
        def latch = new CountDownLatch(1)
        JobBuilder.run("poll") {

            poll {

                runRoute {

                    from("file:${owner.baseDir}?maxMessagesPerPoll=1").process {Exchange exchange ->
                        assert latch.count > 0
                        latch.countDown()
                    }
                }
            }
        }

        latch.await 2, TimeUnit.SECONDS
        assert latch.count == 0
    }
}

