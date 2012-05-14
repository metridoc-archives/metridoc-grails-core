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

import groovy.sql.Sql
import metridoc.dsl.JobBuilder
import org.junit.After
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

    void setupDataSource() {
        dataSource = embeddedDataSource()
        sql = new Sql(dataSource)
        sql.execute(
            """
                CREATE TABLE if not exists `bd_report_distribution` (
                  `email_addr` varchar(32) NOT NULL,
                  `institution_id` int(11) NOT NULL,
                  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                  `version` bigint(20) NOT NULL DEFAULT '0',
                  `library_id` varchar(255) NOT NULL,
                  PRIMARY KEY (`id`),
                  UNIQUE KEY `id` (`id`)
                )
            """
        )

        sql.execute(
            """
                insert into bd_report_distribution (email_addr, institution_id, version, library_id)
                    values ('blah@blah', 123, 0, 123456)
            """
        )

        sql.execute(
            """
                insert into bd_report_distribution (email_addr, institution_id, version, library_id)
                    values ('foo@bar', 321, 1, 654321)
            """
        )
    }

    @After
    void tearDownDataSource() {
        if (binding.variables.containsKey('dataSource')) {
            if (dataSource instanceof DataSource) {
                sql.execute(
                    """
                        drop table bd_report_distribution
                    """
                )
            }
        }
    }

    @Test
    void "test getting emails"() {
        setupDataSource()
        def links = getBorrowDirectReportLinks(sql)
        assert 2 == links.size()
        assert links.containsKey('foo@bar')
        assert links.containsKey('blah@blah')
        assert 123456 == links['blah@blah'] as Integer
        assert 654321 == links['foo@bar'] as Integer
    }


    @Test
    void "if the binding contains the variable dataSource or borrowDirectDataSource, then dataSource is defined"() {
        try {
            BDValidateAndPrepareParameters()
            throw RuntimeException("exception should have occurred")
        } catch (AssertionError assertionError) {}

        assert !borrowDirectDataSourceIsDefined(): "nothing has been set yet, should be false"
        dataSource = [] as DataSource
        assert borrowDirectDataSourceIsDefined(): "dataSource has been set, should be true"
        binding.variables.remove("dataSource")
        binding.variables.remove("borrowDirectDataSource")
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
        def dateRange = computeBorrowDirectDateRange(date)
        assert "20100101/20100131/" == dateRange
    }

    @Test
    void "if the current date is in jan, then the range should be over december"() {
        def date = Date.parse("yyyyMMdd", "20100105")
        def dateRange = computeBorrowDirectDateRange(date)
        assert "20091201/20091231/" == dateRange
    }

    @Test
    void "urls consist of a base url, date range and an institutional identifier"() {
        setupDataSource()
        def date = Date.parse("yyyyMMdd", "20100105")
        def emailByUrl = getBorrowDirectEmailByUrlMap(sql, date)
        assert 2 == emailByUrl.size()
        def baseUrl = "http://metridoc.library.upenn.edu/metridoc-penn-borrow/borrowDirect/reports/dataDump"
        assert "${baseUrl}/20091201/20091231/654321" == emailByUrl['foo@bar']
    }

    @Override
    Object run() {
        JobBuilder.isJob(this)
        includeTargets << _SendUpdateEmail
    }
}
