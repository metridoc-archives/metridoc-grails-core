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

import metridoc.camel.GroovyRouteBuilder
import metridoc.camel.MetridocSimpleRegistry
import metridoc.camel.SqlPlusComponent
import metridoc.utils.CamelUtils
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.component.file.GenericFile
import org.apache.camel.component.file.GenericFileFilter
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.impl.DefaultCamelContextNameStrategy

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 6/25/12
 * Time: 9:42 AM
 */



getEndpoint = {String uri ->
    retrieveCamelContext().getEndpoint(uri)
}

runRoute = {Object[] params ->
    return doRunRoute(params[0])
}

createFileFilter = {Closure closure ->
    [
            accept: {GenericFile file ->
                return closure.call(file)
            }
    ] as GenericFileFilter
}


def retrieveCamelContext() {
    if (!binding.hasVariable("camelScriptingContext")) {
        camelScriptingRegistry = new MetridocSimpleRegistry(binding)
        camelScriptingContext = new DefaultCamelContext(camelScriptingRegistry)
        camelScriptingContext.nameStrategy = new DefaultCamelContextNameStrategy("metridocCamelScripting")
        camelScriptingContext.setLazyLoadTypeConverters(true)
        camelScriptingTemplate = camelScriptingContext.createProducerTemplate()
        camelScriptingContext.addComponent("sqlplus", new SqlPlusComponent(camelScriptingContext))
        camelScriptingContext.start()
    }

    return camelScriptingContext
}

def RouteBuilder doRunRoute(Closure closure) {

    def routeBuilder = new GroovyRouteBuilder()
    routeBuilder.setRoute(closure)

    retrieveCamelContext().addRoutes(routeBuilder)
    CamelUtils.waitTillDone(retrieveCamelContext())

    def exception = routeBuilder.routeException
    if (exception) {
        throw exception
    }

    return routeBuilder
}

def doRunRoute(RouteBuilder routeBuilder) {
    retrieveCamelContext().addRoutes(routeBuilder)
    CamelUtils.waitTillDone(retrieveCamelContext())
}