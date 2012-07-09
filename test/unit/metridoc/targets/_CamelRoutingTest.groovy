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
package metridoc.targets

import metridoc.dsl.JobBuilder
import org.apache.camel.ProducerTemplate
import org.apache.commons.lang.ObjectUtils
import org.junit.Test
import org.apache.camel.component.file.GenericFile

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 6/25/12
 * Time: 9:57 AM
 */
class _CamelRoutingTest {

    @Test
    def void testBasicRouteCall() {
        JobBuilder.run("toMock") {
            target(toMock: "sending data to a mock endpoitn") {
                def mock = getEndpoint("mock:endBasic")
                mock.reset()
                mock.expectedMessageCount(1)
                ProducerTemplate template = camelContext.createProducerTemplate()
                template.requestBody("mock:endBasic", ObjectUtils.NULL)
                mock.assertIsSatisfied()
            }
        }

    }

    @Test
    def void testFullRoute() {
        JobBuilder.run("fooRoute") {
            target(fooRoute: "foo route") {
                def mock = getEndpoint("mock:endFull")
                mock.reset()
                mock.expectedMessageCount(1)
                fileFilter = createFileFilter {GenericFile file ->
                    return file.fileName.startsWith("file")
                }

                def baseDir = "test/unit/metridoc/targets"
                if (!new File("${baseDir}/file1.txt").exists()) {
                    baseDir = "metridoc-reports/${baseDir}"
                }

                runRoute {
                    from("file://${baseDir}?noop=true&initialDelay=0&filter=#fileFilter").threads(4).aggregateBody(4, 2000).to("mock:endFull")
                }

                mock.assertIsSatisfied()
            }
        }
    }

    @Test
    void "test creating a route and sending data to it later" () {
        JobBuilder.run {
            target(default : "the default job") {

            }

            target(createRoute: "creates the route") {
                runRoute {
                    from("direct:hitMeLater").to("mock:gettingDataFromLaterHit")
                }
            }

            target(testDilayedRouting: "tests sending data from a predfined route") {
                def mock = getEndpoint("mock:gettingDataFromLaterHit")
                mock.expectedMessageCount = 1
                template.requestBody("direct:hitMeLater", ObjectUtils.NULL)
                mock.assertIsSatisfied
            }
        }
    }

}
