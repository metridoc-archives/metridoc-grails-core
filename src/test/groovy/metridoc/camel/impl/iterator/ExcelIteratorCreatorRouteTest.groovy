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
package metridoc.camel.impl.iterator

import metridoc.dsl.JobBuilder
import metridoc.test.BaseTest
import org.junit.Test

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 4/14/11
 * Time: 2:29 PM
 * To change this template use File | Settings | File Templates.
 */
class ExcelIteratorCreatorRouteTest extends BaseTest {

    @Test
    def void testRoutingExtension() {
        JobBuilder.run("xlsTest") {

            xlsTest {
                runJobs "setup", "testRoute"
            }

            setup {

                def file = new File("target/test-classes/testFiles/metridoc/camel/impl/iterator/excel/smallTestFile.xls")

                if (!file.exists()) {
                    //then we are running from the trunk, probably using intellij
                    file = new File("plugins/metridoc-camel-plugin/target/test-classes/testFiles/metridoc/camel/impl/iterator/excel/smallTestFile.xls")
                    directory = "plugins/metridoc-camel-plugin/target/test-classes/testFiles/metridoc/camel/impl/iterator/excel"
                } else {
                    directory = "target/test-classes/testFiles/metridoc/camel/impl/iterator/excel"
                }

                assert file.exists()
                assert new File(directory).exists()
            }

            testRoute {
                runRoute {
                    from("file://${directory}?initialDelay=0").splitByXlsRecord().to("mock:end")
                }

                def mock = metridocCamelContext.getEndpoint("mock:end")
                assert mock.exchanges.size() == 3
                mock.exchanges.each {exchange ->
                    def String[] convertedBody = exchange.in.getBody(String[].class)
                    assert convertedBody != null
                }

            }
        }
    }
}
