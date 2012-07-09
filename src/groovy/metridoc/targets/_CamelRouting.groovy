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
import metridoc.camel.MetridocCamelContext
import metridoc.camel.MetridocSimpleRegistry
import metridoc.utils.CamelUtils
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.component.file.GenericFile
import org.apache.camel.component.file.GenericFileFilter

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 6/25/12
 * Time: 9:42 AM
 */

if (!binding.hasVariable("camelContext")) {
    registry = new MetridocSimpleRegistry(binding)
    camelContext = new MetridocCamelContext(registry)
    template = camelContext.createProducerTemplate()
    camelContext.start()
}

getEndpoint = {String uri ->
    camelContext.getEndpoint(uri)
}

runRoute = {Object[] params ->
    if(params[0] instanceof Closure) {
        return doRunRoute(params[0])
    }

    return doRunRoute(params[0], params[1])
}

createFileFilter =  {Closure closure ->
    [
        accept: {GenericFile file ->
            return closure.call(file)
        }
    ] as GenericFileFilter
}


def RouteBuilder doRunRoute(Closure closure) {

    def routeBuilder = new GroovyRouteBuilder()
    routeBuilder.setRoute(closure)

    camelContext.addRoutes(routeBuilder)
    CamelUtils.waitTillDone(camelContext)

    def exception = routeBuilder.routeException
    if (exception) {
        throw exception
    }

    return routeBuilder
}

def doRunRoute(RouteBuilder routeBuilder, Closure closure) {
    camelContext.addRoutes(routeBuilder)
}