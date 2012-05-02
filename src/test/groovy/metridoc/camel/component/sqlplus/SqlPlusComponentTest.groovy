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
package metridoc.camel.component.sqlplus

import org.junit.Test
import javax.sql.DataSource
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.impl.SimpleRegistry
import org.apache.camel.CamelContext;

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 8/4/11
 * Time: 2:14 PM
 */
public class SqlPlusComponentTest {

    @Test(expected = IllegalArgumentException.class)
    void getDataSourceThrowsExceptionIfNotThere() {
        component().getDataSourceReference([:])
    }

    @Test
    void testExtractingColumnsFromMap() {
        String[] columns = component().getColumns([columns: "foo,bar  ,foobar   "])
        assert 3 == columns.size()
        assert "foo" == columns[0]
        assert "bar" == columns[1]
        assert "foobar" == columns[2]
    }

    @Test
    void getDataSourceRemovesDataSourceReference() {
        def dataSource = [:] as DataSource
        def params = ["dataSource": dataSource]
        component().getDataSourceReference(params)
        assert !params.containsKey("dataSource")
    }

    @Test
    void getDataSourceRetrievesDataSourceFromRegistry() {
        def dataSource = [:] as DataSource
        def camelContext = context(testDataSource: dataSource)
        assert dataSource == component(camelContext: camelContext).getDataSource(["dataSource": "testDataSource"])
    }

    @Test(expected = IllegalArgumentException.class)
    void exceptionThrownIfReferenceDoesNotGetDataSourceFromRegistry() {
        component().getDataSource(["dataSource": "testDataSource"])
    }

    @Test
    void defaultBatchReturnedIfNotSpecified() {
        assert SqlPlusComponent.DEFAULT_BATCH_SIZE == component().getBatchSize([:])
    }

    @Test
    void specifiedBatchSizeIsReturned() {
        assert 15 == component().getBatchSize(["batchSize": 15])
    }

    @Test
    void defaultFetchSizeIsReturned() {
        assert SqlPlusComponent.DEFAULT_FETCH_SIZE == component().getFetchSize([:])
    }

    @Test
    void specifiedFetchSizeIsReturned() {
        assert 15 == component().getFetchSize(["fetchSize": 15])
    }

    @Test
    void specifyingMinReturnsLowestFetchSize() {
        assert Integer.MIN_VALUE == component().getFetchSize(["fetchSize": "min"])
    }

    @Test
    void noDuplicateColumnTransfersToEndpoint() {
        def registry = new SimpleRegistry([dataSource: [] as DataSource])
        def component = component(camelContext: new DefaultCamelContext(registry))
        SqlPlusEndpoint endpoint = component.createEndpoint("sqlplus:foo", "dataSource=dataSource", [noDuplicateColumn: "bar", dataSource: "dataSource"])
        assert "bar" == endpoint.noDuplicateColumn
    }

    CamelContext context(LinkedHashMap args = null) {
        if (args) {
            def registry = new SimpleRegistry()
            args.each {key, value ->
                registry[key] = value
            }
            return new DefaultCamelContext(registry)
        }

        return new DefaultCamelContext()
    }

    SqlPlusComponent component(LinkedHashMap args = null) {
        if (args) {
            return new SqlPlusComponent(args)
        }

        return new SqlPlusComponent(camelContext: new DefaultCamelContext(new SimpleRegistry()))
    }
}
