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

import metridoc.utils.PropertyUtils

loadProperties = {Object[] params ->
    assert params.size() > 0: "there must be at least one parameter in loadProperties"
    def firstValueIsBoolean = params[0] instanceof Boolean
    def badArgs = firstValueIsBoolean && params.size() < 2
    assert !badArgs: "loadProperties must have either a list of files, or a boolean with a list of files"

    if (firstValueIsBoolean) {
        doLoadProperties(params[0], params[1..params.size() - 1] as String[])
    } else {
        doLoadProperties(params as String[])
    }
}

def doLoadProperties(String... propertyFiles) {
    def util = new PropertyUtils()
    util.loadProperties(binding, propertyFiles)
}

def doLoadProperties(boolean ignoreFileNotFound, String... propertyFiles) {
    try {
        loadProperties(propertyFiles)
    } catch (FileNotFoundException ex) {
        if (ignoreFileNotFound) {
            binding.message("property-tool", "Could not find file(s) ${propertyFiles}")
        } else {
            throw ex
        }
    }
}

doConfigure = {Closure closure ->
    return new ConfigSlurper().parse(new WrappedClosureScript(binding, closure))
}

class WrappedClosureScript extends Script {

    Closure closure

    WrappedClosureScript(Closure closure) {
        this.closure = closure
    }

    WrappedClosureScript(Binding binding, Closure closure) {
        super(binding)
        this.closure = closure
    }

    @Override
    Object run() {
        closure.setDelegate(this)
        closure.setDirective(Closure.DELEGATE_FIRST)
        closure.setResolveStrategy(Closure.DELEGATE_FIRST)
        closure.run()
    }
}
