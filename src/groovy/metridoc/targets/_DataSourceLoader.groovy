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
package metridoc.targets

import org.apache.commons.dbcp.BasicDataSource
import org.apache.commons.lang.SystemUtils

getDatabaseDrivers = {
    result = []
    driverDirectory.eachFile {
        if (it.name.endsWith(".jar")) {
            result.add(it.toURI().toURL())
        }
    }

    return result
}


driverDirectory = new File("${SystemUtils.USER_HOME}/.grails/drivers")


target(loadDrivers: "loads any drivers that are under <grails.home>/drivers into the root loader") {
    //TODO: should make this relative to the grailsWorkDir variable
    getDatabaseDrivers().each {
        grailsConsole.info "loading driver $it"
        rootLoader.addURL(it)
    }
}

target(configureDataSources: "finds all variables in the config that start with dataSource and creates dataSources from them") {
    depends(loadDrivers)
    assert binding.hasVariable("config"): "config has not been set yet"
    config.each {key, value ->
        if (key.startsWith("dataSource")) {
            def params = extractDataSourceParameters(key)
            config."$key" = createDataSource(params)
        }
    }
}

extractDataSourceParameters = {
    def result = [:]

    if (config."$it") {
        result.username = config."$it".username
        result.password = config."$it".password
        result.driverClassName = config."$it".driverClassName
        result.url = config."$it".url
    }

    return result
}

createDataSource = {LinkedHashMap args ->
    return new BasicDataSource(args)
}
