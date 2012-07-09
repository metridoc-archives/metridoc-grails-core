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

import metridoc.utils.ClassUtils
import org.apache.shiro.SecurityUtils

/**
 * Helps to view available applications a user might have access to
 */
class ControllerHelperService {
    def grailsApplication
    public static final String CONTROLLER = " Controller"

    def getApplications() {
        TreeMap result = [:]

        wrappedControllerClasses.each {
            if (isPermitted(it.getName())) {
                def formattedNaturalName = formatNaturalName(it.naturalName)
                def reportName = ClassUtils.getStaticVariable(it.clazz, "reportName", formattedNaturalName)
                result.put(reportName, it.getName())
            }
        }

        return result
    }

    def isPermitted(String applicationName) {
        def formattedPermission = formatAppNameForPermissionCheck(applicationName)
        SecurityUtils.subject.isPermitted(formattedPermission)
    }

    def getWrappedControllerClasses() {
        grailsApplication.controllerClasses
    }

    def formatNaturalName(String name) {
        if (name.endsWith(CONTROLLER)) {
            int index = name.lastIndexOf(CONTROLLER)
            return name.substring(0, index)
        }

        return name
    }

    def formatAppNameForPermissionCheck(String applicationName) {
        if (applicationName.contains(":")) {
            return applicationName
        }

        else return "${applicationName}:index"
    }
}
