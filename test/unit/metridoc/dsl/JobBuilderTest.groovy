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
package metridoc.dsl

import org.apache.tools.ant.Project
import org.junit.Test

class JobBuilderTest {

    def static boolean doFooCalled = false
    def static boolean doBarCalled = false
    def static boolean fooClosureCalled = false;

    private String baseDir

    def static reset() {
        doBarCalled = false
        doFooCalled = false
        fooClosureCalled = false
    }

    @Test
    def void testRunningAJobWithMainName() {

        def calledMain = false
        JobBuilder.run("main") {
            target(main: "the main guy to run") {
                calledMain = true
            }
        }

        assert calledMain

    }

    @Test
    def void testExceptionWhenPropertyIsMissing() {
        try {
            JobBuilder.run {
                target(default: "default job to run") {
                    stuff = blah //should cause a problem since blah has not been loaded
                }
            }
            fail("exception should have occurred")
        } catch (Exception ex) {
        }
    }

    @Test
    def void testDependsOn() {

        boolean fooRan = false
        JobBuilder.run {
            target(default: "default job") {
                depends(foo)
            }

            target(foo: "foo job") {
                fooRan = true
            }
        }

        assert fooRan

    }

    @Test
    void testNestedDependsOn() {
        boolean barRan = false
        JobBuilder.run {
            target(default: "default job") {
                depends(foo)
            }

            target(foo: "foo job") {
                depends(bar)
            }

            target(bar: "bar job") {
                barRan = true
            }
        }
        assert barRan
    }

    @Test
    void makeSureThereIsALogInBinding() {
        def job = JobBuilder.job()
        assert job.log
    }
}

