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
package metridoc.plugins

import groovy.util.logging.Slf4j
import metridoc.utils.PropertyUtils

/**
 * Plugin to load properties from the classpath ro file system. For more details please see
 * <a href="http://code.google.com/p/metridoc/wiki/PropertyPlugin" target="_blank">http://code.google.com/p/metridoc/wiki/PropertyPlugin</a>
 */
@Plugin(category = "job")
@Slf4j
class PropertyPlugin {

    def static loadProperties(Script script, String... propertyFiles) {
        def util = new PropertyUtils()
        def binding = script.getBinding()
        util.loadProperties(binding, propertyFiles)
    }

    def static loadProperties(Script script, boolean ignoreFileNotFound, String... propertyFiles) {
        try {
            loadProperties(script, propertyFiles)
        } catch (FileNotFoundException ex) {
            if (ignoreFileNotFound) {
                log.warn("Could not find file(s) ${propertyFiles}")
            } else {
                throw ex
            }
        }
    }

    static ConfigObject config(Script script, Closure closure) {
        return new ConfigSlurper().parse(new WrappedClosureScript(script.binding, closure))
    }
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
