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
package metridoc.dsl.routing

import org.junit.Test
import org.junit.Before
import org.apache.camel.Exchange
import metridoc.dsl.JobBuilder

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 6/15/11
 * Time: 3:14 PM
 */
class ExtensionTesting {

    boolean gotThrough = false
    boolean otherwise = false

    @Before
    def void reset() {
        gotThrough = false
        otherwise = false
    }

    @Test
    def void testWhenFiltering() {
        JobBuilder.run {
            defaultJob {
                runRoute {
                    from("direct:start").choice().when {
                        int body = it.in.body
                        body > 0
                    }.process {
                        gotThrough = true
                        otherwise = false
                    }.otherwise().process {
                        gotThrough = false
                        otherwise = true
                    }
                }

                runRoute("direct:start", 0)
                assert otherwise
                assert !gotThrough
                runRoute("direct:start", 1)
                assert !otherwise
                assert gotThrough

            }
        }
    }

    @Test
    def void testFiltering() {
        JobBuilder.run {
            defaultJob {
                runRoute {
                    from("direct:start").filter {Exchange exchange ->
                        int body = exchange.in.body
                        body > 0
                    }.process {
                        gotThrough = true
                    }
                }

                runRoute("direct:start", -1)
                assert !gotThrough
                runRoute("direct:start", 1)
                assert gotThrough
            }
        }
    }
}
