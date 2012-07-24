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

import metridoc.ReportController
import metridoc.admin.AdminController
import metridoc.utils.ClassUtils
import org.apache.shiro.SecurityUtils
import org.codehaus.groovy.grails.commons.GrailsClass

/**
 * Helps to view available applications a user might have access to
 */
class ControllerHelperService {
    def grailsApplication
    public static final String CONTROLLER = " Controller"

    def filterApplications(Closure filter) {
        TreeMap result = [:]
        wrappedControllerClasses.each {
            def passesFilter = filter(it)
            if (isPermitted(it) && passesFilter) {
                def formattedNaturalName = formatNaturalName(it.naturalName)
                def reportName = ClassUtils.getStaticVariable(it.clazz, "reportName", formattedNaturalName)
                result.put(reportName, it.getName())
            }
        }

        return result
    }

    def isPermitted(GrailsClass grailsClass) {
        def isAdminApp = AdminController.isAssignableFrom(grailsClass.getClazz())
        def permissionToken = isAdminApp ? "admin" : grailsClass.getName()
        isPermitted(permissionToken)
    }

    def getReports() {
        return filterApplications {
            return isReport(it.clazz) && !isAdmin(it.clazz)
        }
    }

    def isReport(Class clazz) {
        def hasReportName = ClassUtils.getStaticVariable(clazz, "reportName")
        def hasAdminOnly = ClassUtils.getStaticVariable(clazz, "adminOnly")
        def hasIsReport = ClassUtils.getStaticVariable(clazz, "isReport")
        def isLegacyReport = ReportController.isAssignableFrom(clazz)

        return hasReportName || hasAdminOnly || hasIsReport || isLegacyReport
    }

    def isAdmin(Class clazz) {
        def isLegacyAdmin = AdminController.isAssignableFrom(clazz)
        def isAdmin = ClassUtils.getStaticVariable(clazz, "adminOnly", false)

        return isLegacyAdmin || isAdmin
    }

    def getAdministrativeApps() {
        return filterApplications {
            return isAdmin(it.clazz)
        }
    }

    def getReportsByScope() {
        def result = []
        getAdministrativeApps().keySet().each {
            result.add(
                [
                    name: it,
                    isAdmin: true,
                    isAnonymous: false,
                    isDefault: false
                ]
            )
        }

        return result
    }

    def getApplications() {
        TreeMap result = [:]

        result.putAll(administrativeApps)
        result.putAll(reports)

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
