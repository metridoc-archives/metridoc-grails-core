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

import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 9/5/11
 * Time: 9:19 AM
 */
public class RunnerPluginTest {
    @Test
    void testCallingOtherJobTransfersTheBinding() {
        new FooBar().run()
    }
}

class FooBar extends Script {

    @Override
    Object run() {
        use(RunnerPlugin) {
            fooBar = false
            this.runJob(new CalledJob())
            assert fooBar
        }
    }
}

class CalledJob extends Script {
    @Override
    Object run() {
        assert binding.variables.containsKey("fooBar")
        fooBar = true
    }

}