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
package metridoc.core

import org.codehaus.groovy.grails.commons.GrailsClass
import org.junit.Before
import org.junit.Test
import metridoc.ReportController

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 6/27/12
 * Time: 1:31 PM
 */
class ControllerHelperServiceTest {

    @Delegate
    ControllerHelperService controllerHelperService = new ControllerHelperService()

    @Before
    void "add mocking behavior"() {
        controllerHelperService.metaClass.isPermitted = {String name ->
            this.isPermitted(name)
        }

        controllerHelperService.metaClass.getWrappedControllerClasses = {
            this.getControllerClasses()
        }
    }

    @Test
    void "test retrieving reports for a user"() {
        assert 1 == getApplications().size()
        assert "FooBar" == getApplications()["Foo Bar"]
    }

    @Test
    void "test formatting the natural name"() {
        assert "Foo Bar" == formatNaturalName("Foo Bar Controller")
    }

    @Test
    void "test formatting an application name for permission check"() {
        assert "foo:index" == formatAppNameForPermissionCheck("foo")
        assert "foo:bar" == formatAppNameForPermissionCheck("foo:bar")
    }

    def isPermitted(String applicationName) {
        return true
    }

    def getControllerClasses() {
        def classes = []
        classes.add(
            [
                getName: {"FooBar"},
                getNaturalName: {"Foo Bar Controller"},
                getClazz: {Foo}
            ] as GrailsClass
        )

        return classes
    }
}

class Foo extends ReportController{

}
