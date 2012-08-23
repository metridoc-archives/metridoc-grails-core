import grails.build.logging.GrailsConsole

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
eventCompileStart = {
    def baseDirText = basedir as String
    def pluginDirText = metridocCorePluginDir as String
    def isMetridocCorePlugin = (baseDirText == pluginDirText)
    GrailsConsole grailsConsole = new GrailsConsole()
    if (isMetridocCorePlugin) {
        grailsConsole.info "Deleting src/templates/conf dir "
        ant.delete(dir:"${basedir}/src/templates/conf")
        grailsConsole.info "Building new src/templates/conf dir"
        ant.copy(todir:"${basedir}/src/templates/conf") {
            fileset(dir:"${basedir}/grails-app/conf") {
                include(name:"Config.groovy")
                include(name:"DataSource.groovy")
                include(name: "UrlMappings.groovy")
            }
        }
    }
}