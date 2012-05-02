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

package metridoc.camel.test

import org.apache.camel.CamelContext
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.impl.SimpleRegistry
import org.apache.camel.test.junit4.CamelTestSupport

/**
 *
 * @author Tommy Barker
 */
class RouteTest extends CamelTestSupport {
    protected SimpleRegistry registry = new SimpleRegistry();

    protected boolean routeTracingEnabled() {
        return false;
    }


    protected boolean addExtensions() {
        return false
    }

    @Override
    protected CamelContext createCamelContext() throws Exception {
        CamelContext camelContext = new DefaultCamelContext(registry);
        camelContext.setTracing(routeTracingEnabled());
        return camelContext;
    }
}

