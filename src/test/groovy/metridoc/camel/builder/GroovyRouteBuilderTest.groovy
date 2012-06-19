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

package metridoc.camel.builder

import metridoc.dsl.JobBuilder
import metridoc.test.BaseTest
import org.junit.Test

/**
 *
 * @author Tommy Barker
 */
class GroovyRouteBuilderTest extends BaseTest{

    def job = JobBuilder.job()

    @Test
    def void testErrorHandlingInEndToEndTest() {
        try {
            JobBuilder.run("callException") {

                callException {
                    runJobs "exception"
                }

                exception {
                    runRoute {
                        errorHandler(noErrorHandler())
                        from("direct:fail").process {
                            assert 1 == 2
                        }
                    }
                    runRoute "direct:fail"
                }
            }

            fail("exception should have occurred")
        } catch (RuntimeException ex) {
        }
    }

    @Test
    def void testErrorWhenItIsInAnotherThread() {
        try {
            JobBuilder.run {
                defaultJob {
                    runRoute {
                        errorHandler(noErrorHandler())
                        from("direct:fail").threads(5).to("seda:failOnDifferentThread")
                        from("seda:failOnDifferentThread").throwException(new RuntimeException("I meant to do that"))
                    }

                    runRoute "direct:fail"
                }
            }

            fail("exception should have occurred")
        } catch (Exception ex) {
        }
    }
}

