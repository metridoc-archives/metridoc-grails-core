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

import metridoc.test.BaseTest
import org.apache.tools.ant.Project
import org.junit.Test

class JobBuilderTest extends BaseTest {

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
            main {
                calledMain = true
            }
        }

        assert calledMain

    }

    @Test
    def void testExceptionWhenPropertyIsMissing() {
        try {
            JobBuilder.run {
                defaultJob {
                    stuff = blah //should cause a problem since blah has not been loaded
                }
            }
            fail("exception should have occurred")
        } catch (Exception ex) {

        }
    }

    @Test
    def void testUsingMain() {
        def mainCalled = false

        JobBuilder.run {
            defaultJob {
                mainCalled = true
            }
        }

        assert mainCalled
    }

    @Test
    def void testDependsOn() {

        boolean fooRan = false
        JobBuilder.run {
            defaultJob(dependsOn: "foo") {

            }

            foo {
                fooRan = true
            }
        }

        assert fooRan

    }

    @Test
    void testNestedDependsOn() {
        boolean fooRan = false
        JobBuilder.run {
            defaultJob(dependsOn: "foo") {

            }

            foo(dependsOn: "bar") {

            }

            bar {
                fooRan = true
            }
        }
        assert fooRan
    }

    @Test
    void makeSurePluginsGetAdded() {
        boolean fooRan = false
        JobBuilder.run {
            defaultJob {
                runJob("bar")
            }

            bar {
                fooRan = true
            }
        }
        assert fooRan
    }

    @Test
    void makeSureThereIsALogInBinding() {
        def job = JobBuilder.job()
        assert job.log
    }

    @Test
    void testBindingBindingAndProject() {
        def project = new Project()
        def binding = new Binding()
        binding.setVariable("bam", "boom")
        project.addReference("bloom", "blam")
        JobBuilder.bindProjectAndBinding(binding, project)

        project.addReference("foo", "bar")
        assert "bar" == binding.foo
        assert "bar" == project.getReference("foo")

        binding.setVariable("bar", "foobar")
        assert "foobar" == project.getReference("bar")

        assert "boom" == project.getReference("bam")
        assert "blam" == binding.getVariable("bloom")
        assert null == project.getReference("notThere")
    }
}

