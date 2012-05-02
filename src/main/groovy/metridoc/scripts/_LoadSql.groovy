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

import groovy.sql.Sql
import metridoc.dsl.JobBuilder
import metridoc.utils.ClassUtils

import javax.sql.DataSource
import org.codehaus.gant.GantState

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 4/28/12
 * Time: 10:32 AM
 * To change this template use File | Settings | File Templates.
 */

JobBuilder.isJob(this)
includeTargets << _BaseUtilities

getDatabaseType = {DataSource dataSource ->
    def url = dataSource.getConnection().getMetaData().getURL()
    log.debug "extracting database type from ${url}"
    def m = url =~ "jdbc:([^:]+)"
    m.lookingAt()
    return m.group(1)
}

createQueryHandler = {DataSource dataSource, String sqlFile = null ->
    def queryHandler = [:] as HashWithFailOnMissingEntry
    queryHandler.sql = new Sql(dataSource)
    def classLoader = ClassUtils.defaultClassLoader

    if(!sqlFile) {
        def databaseType = getDatabaseType(dataSource)
        sqlFile = "sql/${databaseType}.groovy"
    }

    classLoader.getResources(sqlFile).each {URL url ->
        GroovyShell shell = new GroovyShell(classLoader)
        Script script = shell.parse(url.text)
        script.run()

        script.binding.variables.each {key, value ->

            if (value instanceof Closure) {
                queryHandler[key] = value
                value.delegate = queryHandler
                value.resolveStrategy = Closure.DELEGATE_FIRST
            }
        }
    }

    return queryHandler
}

target(loadSqlFile:
    """
        loads an sql file based on dataSource type.  Assumes the variable 'dataSource'
        is the primary DataSource.  Extracts out the database type from datasource and loads
        all sql files related to it.  The variable queryHandler is created from the load
    """
) {
    queryHandler = createQueryHandler(dataSource, getDatabaseType(dataSource))
}



