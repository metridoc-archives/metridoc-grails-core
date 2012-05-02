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
package metridoc.workflows.validation.impl

import groovy.sql.Sql
import metridoc.utils.DataSourceUtils
import org.apache.camel.test.junit4.CamelTestSupport
import org.junit.Test
import static FileIngestorValidationErrorHandler.DATASOURCE_CANNOT_BE_NULL
import static FileIngestorValidationErrorHandler.SCHEMAS_CANNOT_BE_NULL_OR_EMPTY

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 2/14/12
 * Time: 9:22 AM
 */
class FileIngestorValidationErrorHandlerTest extends CamelTestSupport {

    def dataSource = DataSourceUtils.embeddedDataSource()

    @Test
    void errorOccursIfDataSourceIsNull() {
        testError(DATASOURCE_CANNOT_BE_NULL) {
            new FileIngestorValidationErrorHandler().updateSchema()
        }
    }

    @Test
    void errorOccursIfSchemasIsEmpty() {
        testError(SCHEMAS_CANNOT_BE_NULL_OR_EMPTY) {
            new FileIngestorValidationErrorHandler(dataSource: dataSource, validationErrorSchemas: null).updateSchema()
        }
    }

    @Test
    void testStoringAnAssertionError() {
        def error = new AssertionError()
        def scope = "just testing"
        def handler = new FileIngestorValidationErrorHandler(dataSource: dataSource, validationErrorScope: scope)
        handler.updateSchema()
        handler.handle(error, createExchangeWithBody("record"))
        def sql = new Sql(dataSource)
        assert 1 == sql.firstRow("select count(*) as total from validation_errors").total
        def row = sql.firstRow("select * from validation_errors")
        assert "just testing" == row.scope
    }

    void testError(String message, Closure closure) {
        try {
            closure.call()
            assert false: "exception should have occurred"
        } catch (AssertionError assertionError) {
            assert assertionError.message.startsWith(message)
        }
    }
}
