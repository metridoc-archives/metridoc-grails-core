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
package metridoc.workflows

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 2/20/12
 * Time: 2:43 PM
 *
 * Provides a base test to handle common testing scenarios
 */
class BaseTestHelper {

    def testForAssertionError(String message, Closure closure) {
        assert message: "Cannot test for assertion errors without providing a message to test against"
        try {
            closure.call()
            assert false: "exception should have occurred"
        } catch (AssertionError error) {
            assert error.message.startsWith(message)
        }
    }
}
