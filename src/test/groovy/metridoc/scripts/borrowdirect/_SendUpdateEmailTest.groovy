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
package metridoc.scripts.borrowdirect

import metridoc.dsl.JobBuilder
import org.junit.Before
import org.junit.Test

import javax.sql.DataSource

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 5/7/12
 * Time: 1:44 PM
 */
class _SendUpdateEmailTest extends Script {

    @Before
    void setup() {
        run()
    }

    @Test
    void "if the binding contains the variable dataSource or borrowDirectDataSource, then dataSource is defined"() {
        assert !borrowDirectDataSourceIsDefined(): "nothing has been set yet, should be false"
        dataSource = [] as DataSource
        assert borrowDirectDataSourceIsDefined(): "dataSource has been set, should be true"
        binding.variables.remove("dataSource")
        assert !borrowDirectDataSourceIsDefined(): "dataSource was removed, should be false"
        borrowDirectDataSource = [] as DataSource
        assert borrowDirectDataSourceIsDefined(): "borrowDirectDataSource was added, should be true"
    }

    @Test
    void "if the dataSource has been set, it should be a DataSource"() {
        dataSource = []
        assert !borrowDirectDataSourceIsDefined()
    }

    @Test
    void "if the current date is in feb, then the range should be over january"() {
        def date = Date.parse("yyyyMMdd", "20100215")
        def dateRange = borrowDirectDateRange(date)
        assert "20100101/20100131/" == dateRange
    }

    @Test
    void "if the current date is in jan, then the range should be over december"() {
        def date = Date.parse("yyyyMMdd", "20100105")
        def dateRange = borrowDirectDateRange(date)
        assert "20091201/20091231/" == dateRange
    }

    @Override
    Object run() {
        JobBuilder.isJob(this)
        includeTargets << _SendUpdateEmail
    }
}
