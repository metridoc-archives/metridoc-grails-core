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
package metridoc.plugins.schema

import org.junit.Test

import javax.sql.DataSource

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 7/30/11
 * Time: 12:52 PM
 */
public class MetridocLiquibaseTest {

    @Test
    void aClassLoaderIsRetrievableIfNotSet() {
        def liquibase = new MetridocLiquibase()
        assert liquibase.classLoader
    }

    @Test
    void theSameClassLoaderIsRetrievedEveryTime() {
        def liquibase = new MetridocLiquibase()
        def classLoader = liquibase.classLoader
        assert classLoader == liquibase.classLoader
    }

    @Test
    void theClassLoaderThatIsSetIsReturned() {
        def classLoader = [:] as ClassLoader
        def liquibase = new MetridocLiquibase(classLoader: classLoader)
        assert classLoader == liquibase.classLoader
    }

    @Test(expected = IllegalStateException)
    void illegalStateIfRunningMigrationAndNoDataSourceIsSet() {
        liquibase().runMigration()
    }

    @Test(expected = IllegalStateException)
    void illegalStateIfRunningMigrationAndNoChangeLogSet() {
        liquibase(dataSource: [:] as DataSource).runMigration()
    }



    MetridocLiquibase liquibase(LinkedHashMap args = null) {
        if (args) {
            return new MetridocLiquibase(args)
        }
        return new MetridocLiquibase()
    }

}
