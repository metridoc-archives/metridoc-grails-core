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
package metridoc.camel

import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.impl.DefaultCamelContextNameStrategy
import org.apache.camel.impl.SimpleRegistry
import org.apache.camel.spi.Registry
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 2/24/12
 * Time: 11:12 AM
 */
class MetridocCamelContext extends DefaultCamelContext {

    boolean stopAtShutdown = true
    private final transient Logger log = LoggerFactory.getLogger(getClass());
    private static MetridocCamelContext defaultInstance


    static MetridocCamelContext instance() {
        if (defaultInstance) {
            return defaultInstance
        }

        defaultInstance = new MetridocCamelContext()
        defaultInstance.start()

        return defaultInstance
    }

    MetridocCamelContext() {
        this(new SimpleRegistry())
    }

    MetridocCamelContext(Registry registry) {
        super(registry)
        this.setLazyLoadTypeConverters(true)
        this.disableJMX()
        this.nameStrategy = new DefaultCamelContextNameStrategy("metridocCamel")
    }
}
