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
package metridoc.admin

import org.apache.commons.lang.StringUtils
import metridoc.reports.ShiroRole

class ManageReportsController {

    def grailsApplication
    def static reportName = "Report Manager"
    def static adminOnly = true
    def manageReportsService

    def intro = "Change the security setting of reports by declaring them as anonymous, admin or neither." +
            " Apps that are not administrative or anonymous will use the default security"
    def updatedMsg = "Report Security Updated";
    static messages = [] as Set

    def index() {
        messages << intro
        flash.messages = messages

        def controllersInCore = [] as List<String>;
        grailsApplication.controllerClasses.each {
            def clazz = it.clazz
            def artifactName = StringUtils.uncapitalize(it.name)
            controllersInCore << artifactName
        }
        def reportsInCore = [] as List
        ReportsConfiguration.list().each {report ->
            if (controllersInCore.contains(StringUtils.uncapitalize(report.name))) {
                reportsInCore << report
            }
        }
        def roleList = [] as List
        ShiroRole.list().each {
            roleList << it.name
        }
        [
                reports: reportsInCore,
                roles: roleList
        ]
    }

    def updateReportSecurity() {
        manageReportsService.updateRoles(params as Map)
        messages << updatedMsg
        chain(action: "index")
    }


}
