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
package metridoc.jenkins

import metridoc.annotations.ReportApplication
import org.apache.commons.lang.exception.ExceptionUtils
import org.apache.commons.lang.SystemUtils

@ReportApplication("jenkins")
class JenkinsController {

    def jenkinsService

    def index() {
        def errorMessage = jenkinsService.checkJenkins()
        log.info "chenkJenkins(): ${errorMessage}"
        render(view: 'jenkins', model: [errorMessage: errorMessage])
    }

    def runJenkins() {
        def errorMessage = jenkinsService.runJenkins()
        if ("jenkinsRunning".equals(errorMessage)) {
            render(view: 'jenkins', model: [errorMessage: errorMessage])
        } else {
            render "${errorMessage}"
        }
    }

    def download() {
        def errorMessage = jenkinsService.downloadJenkins()
        if ("jenkinsNotRun".equals(errorMessage)) {
            render(view: 'jenkins', model: [errorMessage: errorMessage])
        } else {
            render "${errorMessage}"
        }
    }

}
