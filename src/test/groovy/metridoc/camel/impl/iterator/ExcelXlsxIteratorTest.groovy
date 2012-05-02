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

import org.junit.Test
import metridoc.test.BaseTest

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 7/17/11
 * Time: 2:54 PM
 */
class ExcelXlsxIteratorTest extends BaseTest{

    def File getFile() {
        def fromTrunk = "plugins/metridoc-camel/metridoc-camel-core"
        def fromProject = "target/test-classes/testFiles/metridoc/camel/impl/iterator/xlsx/xlsxTest.xlsx"
        def file = new File("${fromTrunk}/${fromProject}")
        if(file.exists()) {
            return file
        }

        return new File("${fromProject}")
    }

    @Test
    def void testIterator() {
        def inputStream = new FileInputStream(getFile())
        def xlsxIteratorCreator = new ExcelXlsxIteratorCreator(inputStream: inputStream)
        assert xlsxIteratorCreator.hasNext()
        xlsxIteratorCreator.close()
    }
}
