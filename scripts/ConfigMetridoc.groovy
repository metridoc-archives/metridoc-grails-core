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
import org.codehaus.gant.GantBuilder

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
includeTargets << grailsScript("_GrailsInit")
includeTargets << grailsScript("_GrailsArgParsing")

target(main: "Config metridoc-reports plugin") {
    depends(parseArguments)
    def copyFileFromMap = new HashMap<File, String>();

    mapFile(copyFileFromMap, "${basedir}/grails-app/conf/Config.groovy", "${metridocCorePluginDir}/src/templates/conf/Config.groovy")
    mapFile(copyFileFromMap, "${basedir}/grails-app/conf/DataSource.groovy", "${metridocCorePluginDir}/src/templates/conf/DataSource.groovy")
    mapFile(copyFileFromMap, "${basedir}/grails-app/conf/UrlMappings.groovy", "${metridocCorePluginDir}/src/templates/conf/UrlMappings.groovy")
    mapFile(copyFileFromMap, "${basedir}/grails-app/conf/spring/resources.groovy", "${metridocCorePluginDir}/src/templates/conf/spring/resources.groovy")

    copyFileFromMap.each {toFile, fromFile ->
        def splitName = toFile.name.split("/")
        def name = splitName[splitName.size()-1]
        if ((!argsMap['o'])&&(toFile.exists())) {
            ant.input(addproperty: "ifOverWrite${name}", validargs:"y,n", message: "${toFile} already exists, do you want overwrite it?[y->yes,n->no]:")
        }
        if ((argsMap['o'])||(ant.project.getProperty("ifOverWrite${name}") != 'n')) {
            println "copying ${fromFile} to ${toFile}"
            ant.mkdir(dir: toFile.getCanonicalPath().replace("/${name}",""))
            ant.copy(file: "${fromFile}",
                    tofile: "${toFile.getCanonicalPath()}")
        }else{
            println "Ignored ${name}"
        }
    }
}

void mapFile(HashMap<File, String> fileMap, String toFile, String fromFile){
    fileMap.put(new File("${toFile}"), fromFile)
}

setDefaultTarget(main)
