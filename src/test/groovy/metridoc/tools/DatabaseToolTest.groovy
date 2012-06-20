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
package metridoc.tools

import metridoc.dsl.JobBuilder
import org.codehaus.gant.GantBinding
import org.junit.Before
import org.junit.Test

import java.sql.Connection
import java.sql.DatabaseMetaData
import javax.sql.DataSource

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 6/19/12
 * Time: 9:10 AM
 */
class DatabaseToolTest {

    def binding = new GantBinding()
    def tool = new DatabaseTool(binding)
    def fooUrl = "jdbc:foo:bar"

    @Before
    void setupBinding() {
        binding.toDataSource = fooDataSource
        binding.dataSource = barDataSource
    }

    @Test(expected = AssertionError)
    void "if the variable in the binding is not a datasource, then an assertion error is thrown"() {
        binding.toDataSource = "some string"
        tool.getDatabaseType()
    }

    @Test
    void "test order of dataSource resolution"() {
        assert "foo" == tool.getDatabaseType()
    }

    @Test
    void "with a dataSource with a url jdbc:foo:bar, the databaseType is foo"() {
        assert "foo" == tool.getDatabaseType(fooDataSource)
    }

    @Test
    void "if url is jdbc:foo:bar, then dataBaseType is foo"() {
        assert "foo" == tool.getDatabaseType(fooUrl)
    }

    @Test(expected = AssertionError)
    void "if the url is invalid, like jdbc_blah, then an assertion error is thrown"() {
        tool.getDatabaseType("jdbc_blah")
    }

    @Test
    void "make sure it can be included as a tool"() {
        def script = new DatabaseToolScript()
        script.run()
        assert script.databaseTool
    }

    def fooDataSource = [
        getConnection: {
            fooConnection
        }
    ] as DataSource

    def fooConnection = [
        getMetaData: {
            fooMetaData
        }
    ] as Connection

    def fooMetaData = [
        getURL: {
            "jdbc:foo:bar"
        }
    ] as DatabaseMetaData

    def barDataSource = [
        getConnection: {
            barConnection
        }
    ] as DataSource

    def barConnection = [
        getMetaData: {
            barMetaData
        }
    ] as Connection

    def barMetaData = [
        getURL: {
            fooUrl
        }
    ] as DatabaseMetaData
}

class DatabaseToolScript extends Script {

    @Override
    Object run() {
        JobBuilder.isJob(this)
        includeTool << DatabaseTool
    }
}
