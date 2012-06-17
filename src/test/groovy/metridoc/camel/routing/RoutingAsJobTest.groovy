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
package metridoc.camel.routing

import metridoc.dsl.JobBuilder
import metridoc.test.BaseTest
import metridoc.utils.ObjectUtils
import org.junit.Test

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 7/9/11
 * Time: 7:01 PM
 */
class RoutingAsJobTest extends BaseTest {

    @Test(timeout = 3000L)
    def void testSeda() {
        def job = JobBuilder.job()
        boolean itRan = false

        job.runRoute {
            from("seda:startSedaTest").process {
                itRan = true
            }
        }
        job.runRoute "seda:startSedaTest", ObjectUtils.NULL

        assert itRan
    }

    @Test
    def void testRoutingWithFileExceptions() {
        def job = JobBuilder.job()

        try {
            job.runRoute {
                errorHandler(noErrorHandler())
                from(getBaseDirectory()).process {
                    throw new Exception("oops")
                }
            }
            fail("exception should have occurred")
        } catch (Exception e) {
        }
    }

    def String getBaseDirectory() {
        def testDir = "target/test-classes/testFiles/metridoc/camel/routing"

        def file = new File(testDir)
        if (file.exists()) {
            return getEndpointPath(testDir)
        }

        testDir = "metridoc-core/${testDir}"
        file = new File(testDir)
        assert file.exists()
        return getEndpointPath(testDir)
    }

    def getEndpointPath(String dir) {
        return "file://${dir}?noop=true&maxMessages=50"
    }


}

