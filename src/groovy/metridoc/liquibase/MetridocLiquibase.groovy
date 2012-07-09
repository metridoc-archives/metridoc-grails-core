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
package metridoc.liquibase

import liquibase.Liquibase

import liquibase.exception.DatabaseException

import liquibase.resource.ResourceAccessor
import liquibase.resource.ClassLoaderResourceAccessor
import liquibase.database.Database
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection

import java.sql.Connection
import java.sql.SQLException
import javax.sql.DataSource
import org.springframework.util.ClassUtils

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 7/30/11
 * Time: 12:51 PM
 *
 * runs a liquibase migration.  Used {@link liquibase.integration.spring.SpringLiquibase} as a template to get this to
 * work
 *
 */
class MetridocLiquibase {

    /**
     * change log parameters taht can be set, default is a empty {@link Map}
     */
    Map<String, String> changeLogParameters = [:]
    /**
     * The change log that contains the liquibase migration
     */
    String schema
    /**
     * the classLoader to used to find the change logs, by default it is
     * <code>Thread.currentThread().contextClassLoader</code>
     */
    ClassLoader classLoader
    /**
     * a comma separated list of liquibase contexts to load.  Please see liquibase documentation to see information on
     * contexts.  By default it is null, which runs all of them
     */
    String contexts
    /**
     * the required dataSource for liquibase to connect to and run a migration
     */
    DataSource dataSource

    //TODO: what is a default schema?  Why is this even here?
    /**
     * Default schema
     */
    String defaultSchema
    private static final ERROR_MESSAGE_POSTFIX = "must be specified for liquibase to run"

    /**
     * gets the set classLoader, if not set returns <code>Thread.currentThread().contextClassLoader</code>
     * @return
     */
    ClassLoader getClassLoader() {
        if (classLoader) {
            return classLoader
        }
        classLoader = ClassUtils.defaultClassLoader
    }

    /**
     * runs the liquibase migration
     */
    void runMigration() {
        assert dataSource: "A DataSource ${ERROR_MESSAGE_POSTFIX}"
        assert schema: "A change log ${ERROR_MESSAGE_POSTFIX}"

        //taken from SpringLiquibase
        String shouldRunProperty = System.getProperty(Liquibase.SHOULD_RUN_SYSTEM_PROPERTY);
        if (shouldRunProperty != null && !Boolean.valueOf(shouldRunProperty)) {
            System.out.println("Liquibase did not run because '" + Liquibase.SHOULD_RUN_SYSTEM_PROPERTY + "' system property was set to false");
            return;
        }

        Connection c = null;
        Liquibase liquibase = null;
        try {
            c = getDataSource().getConnection();
            liquibase = createLiquibase(c);
            liquibase.update(getContexts());
        } catch (SQLException e) {
            throw new DatabaseException(e);
        } finally {
            if (liquibase != null) {
                liquibase.forceReleaseLocks();
            }
            if (c != null) {
                try {
                    c.rollback();
                    c.close();
                } catch (SQLException e) {
                    //nothing to do
                }
            }
        }
    }

    private Liquibase createLiquibase(Connection connection) {
        Liquibase liquibase = new Liquibase(getSchema(), createResourceOpener(), createDatabase(connection));
        if (changeLogParameters != null) {
            for (Map.Entry<String, String> entry : changeLogParameters.entrySet()) {
                liquibase.setChangeLogParameter(entry.getKey(), entry.getValue());
            }
        }

        return liquibase;
    }

    private Database createDatabase(Connection connection) {
        Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
        if (this.defaultSchema != null) {
            database.setDefaultSchemaName(this.defaultSchema);
        }
        return database;
    }

    private ResourceAccessor createResourceOpener() {
        return new ClassLoaderResourceAccessor(getClassLoader())
    }
}
