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

import metridoc.dsl.JobBuilder
import org.junit.Test

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 9/6/11
 * Time: 11:20 AM
 * To change this template use File | Settings | File Templates.
 */
public class PropertyPluginTest {

    @Test
    void "error is thrown if loadProperties does not have parameters"() {
        def script = new BadPropertyPluginTest()
        try {
            script.run()
            throw new RuntimeException("exception should have occurred")
        } catch (AssertionError error) {
        }
    }

    @Test
    void "error should occur if the first value is a boolean and there are no other values"() {
        def script = new BadPropertyPluginTest()

        try {
            script.firstValue = true
            script.run()
            throw new RuntimeException("exception should have occurred")
        } catch (AssertionError error) {
        }
    }

    @Test
    void testConfigCreation() {
        def script = new PropertyPluginTestScript()
        script.run()
        assert script.bar == "foobar"
        assert script.config.foo.bar == "bam"
    }
}


class BadPropertyPluginTest extends Script {

    @Override
    Object run() {
        JobBuilder.isJob(this)
        if (binding.variables.firstValue) {
            loadProperties(firstValue)
        } else {
            loadProperties()
        }
    }
}

class PropertyPluginTestScript extends Script {
    @Override
    Object run() {

        JobBuilder.isJob(this)

        bar = "foobar"
        blam = "bam"

        config = config {
            bar {
                blam = "bam"
            }
            foo {
                bar = bar.blam
            }
        }
    }

}