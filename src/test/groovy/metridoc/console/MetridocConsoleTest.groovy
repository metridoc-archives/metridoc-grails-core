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
package metridoc.console

import org.junit.Test

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 2/5/12
 * Time: 8:50 AM
 */
class MetridocConsoleTest {

    @Test
    void testRetrievingConsoleMapping() {
        def mapping = new MetridocConsole().consoleMapping
        assert mapping.containsKey("runJob")
        assert mapping.runJob.newInstance() instanceof Script
        assert mapping.containsKey("testRun")
    }

    @Test
    void testReflection() {
        def clazz = Class.forName("metridoc.console.MetridocConsole")
        def instance = clazz.newInstance()
        def method = clazz.getMethod("runConsoleCommand", String[].class)
        def methodArgument = ["testRun", "foo"] as String[]
        Object[] params = new Object[1]
        params[0] = methodArgument
        method.invoke(instance, params)
    }
}
