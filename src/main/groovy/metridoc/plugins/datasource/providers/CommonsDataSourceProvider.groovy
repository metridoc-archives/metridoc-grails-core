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
package metridoc.plugins.datasource.providers

import metridoc.dsl.JobBuilder
import org.apache.commons.dbcp.BasicDataSource

import javax.sql.DataSource

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 7/12/11
 * Time: 5:44 PM
 */
class CommonsDataSourceProvider {

    static DataSource getDataSource(LinkedHashMap args) {
        if (args.user) {
            args.username = args.remove("user")
        }

        if (args.driver) {
            args.driverClassName = args.remove("driver")
        }

        //pre determine where the class is
        try {
            Class.forName(args.driverClassName)
        } catch (ClassNotFoundException ex) {
            args.driverClassLoader = JobBuilder.getClass().classLoader  //just guessing
        }

        return new BasicDataSource(args)
    }
}
