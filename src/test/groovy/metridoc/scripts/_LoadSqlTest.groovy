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
package metridoc.scripts

import org.junit.Test
import metridoc.dsl.JobBuilder
import org.codehaus.gant.GantState

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 4/28/12
 * Time: 12:08 PM
 * To change this template use File | Settings | File Templates.
 */
class _LoadSqlTest extends Script {

    @Test
    void "getDatabaseType returns the database type based on DataaSource"() {
        run()
        assert "h2" == getDatabaseType(embeddedDataSource())
    }

    @Test
    void "test retrieving sql from file source on classpath"() {
        this.run()

        def handler = createQueryHandler(embeddedDataSource(), "testFiles/metridoc/scripts/foo.groovy")
        handler.createFooTable()
        handler.insertIntoFoo(5)
        assert 1 == handler.selectCountInFoo()
        handler.dropFooTable()
    }

    @Override
    Object run() {
        JobBuilder.isJob(this)
        includeTargets << _LoadSql
    }
}
