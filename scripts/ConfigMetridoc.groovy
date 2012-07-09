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
    def ifOverWriteMap = new HashMap<File, String>();

    def bootStrapFile = new File("${basedir}/grails-app/conf/BootStrap.groovy")
    copyFileFromMap.put(bootStrapFile,"${metridocCorePluginDir}/src/templates/conf/BootStrap.groovy")
    ifOverWriteMap.put(bootStrapFile,'n')
    def configFile = new File("${basedir}/grails-app/conf/Config.groovy")
    copyFileFromMap.put(configFile, "${metridocCorePluginDir}/src/templates/conf/Config.groovy")
    ifOverWriteMap.put(configFile, 'n')
    def buildConfigFile = new File("${basedir}/grails-app/conf/BuildConfig.groovy")
    copyFileFromMap.put(buildConfigFile, "${metridocCorePluginDir}/src/templates/conf/BuildConfig.groovy")
    ifOverWriteMap.put(buildConfigFile, 'n')
    def dataSourceFile = new File("${basedir}/grails-app/conf/DataSource.groovy")
    copyFileFromMap.put(dataSourceFile,"${metridocCorePluginDir}/src/templates/conf/DataSource.groovy")
    ifOverWriteMap.put(dataSourceFile,'n')
    def urlMappingFile = new File("${basedir}/grails-app/conf/UrlMappings.groovy")
    copyFileFromMap.put(urlMappingFile, "${metridocCorePluginDir}/src/templates/conf/UrlMappings.groovy")
    ifOverWriteMap.put(urlMappingFile, 'n')
    def resourcesFile = new File("${basedir}/grails-app/conf/spring/resources.groovy")
    copyFileFromMap.put(resourcesFile, "${metridocCorePluginDir}/src/templates/conf/spring/resources.groovy")
    ifOverWriteMap.put(resourcesFile, 'n')
    def layoutsFile = new File("${basedir}/grails-app/views/layouts/main.gsp")
    copyFileFromMap.put(layoutsFile, "${metridocCorePluginDir}/grails-app/views/layouts/main.gsp")
    ifOverWriteMap.put(layoutsFile, 'n')
    def indexFile = new File("${basedir}/grails-app/views/index.gsp")
    copyFileFromMap.put(indexFile, "${metridocCorePluginDir}/grails-app/views/index.gsp")
    ifOverWriteMap.put(indexFile, 'n')
    def mainCssFile = new File("${basedir}/web-app/css/main.css")
    copyFileFromMap.put(mainCssFile, "${metridocCorePluginDir}/web-app/css/main.css")
    ifOverWriteMap.put(mainCssFile, 'n')
    def mobileCssFile = new File("${basedir}/web-app/css/mobile.css")
    copyFileFromMap.put(mobileCssFile, "${metridocCorePluginDir}/web-app/css/mobile.css")
    ifOverWriteMap.put(mobileCssFile, 'n')
    def errorCssFile = new File("${basedir}/web-app/css/errors.css")
    copyFileFromMap.put(errorCssFile, "${metridocCorePluginDir}/web-app/css/errors.css")
    ifOverWriteMap.put(errorCssFile, 'n')
    def appFile = new File("${basedir}/web-app/js/application.js")
    copyFileFromMap.put(appFile, "${metridocCorePluginDir}/web-app/js/application.js")
    ifOverWriteMap.put(appFile, 'n')

    copyFileFromMap.each {toFile, fromFile ->
        def splitName = toFile.name.split("/")
        def name = splitName[splitName.size()-1]
        if ((!argsMap['o'])&&(toFile.exists())) {
            ant.input(addproperty: "ifOverWrite${name}", validargs:"y,n", message: "${toFile} already exists, do you want overwrite them?[y->yes,n->no]:")
        }
        if ((argsMap['o'])||(ant.project.getProperty("ifOverWrite${name}") != 'n')) {
            println "Config ${name}"
            ant.mkdir(dir: toFile.getCanonicalPath().replace("/${name}",""))
            ant.copy(file: "${fromFile}",
                    tofile: "${toFile.getCanonicalPath()}")
        }else{
            println "Ignored ${name}"
        }
    }

}

setDefaultTarget(main)
