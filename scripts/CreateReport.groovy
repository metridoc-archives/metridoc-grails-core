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

import grails.util.GrailsNameUtils
import groovy.text.XmlTemplateEngine
import groovy.text.SimpleTemplateEngine
import grails.build.logging.GrailsConsole

includeTargets << grailsScript("_GrailsInit")
includeTargets << new File("scripts/ConfigMetridoc.groovy")
includeTargets << new File("scripts/_FileTemplateContent.groovy")

target(main: "create default fileset for user customised report") {
    grailsConsole = new GrailsConsole()

    if (argsMap["params"].size() > 0) {
        name = argsMap["params"][0]
        name.toLowerCase()
        rName = new StringBuilder(name.size())
        rName.append(Character.toUpperCase(name.charAt(0)))
        rName.append(name.substring(1))
        simpleEngine = new groovy.text.SimpleTemplateEngine()
        xmlEngine = new groovy.text.XmlTemplateEngine()
        fileFullName  = ""
        depends(createConfigFile, createRunScript)
        depends(createGSPFile, createStyleFile)
        depends(createResourceFile, createSchemaFile)
        depends(createControllerFile, createServiceFile)
    }else {
        grailsConsole.info("WARN: no report name specified in script 'create-report [reportName]'")
    }

}

/*grails-app/conf/metridoc/FooConfig.groovy (empty)*/
target(createConfigFile:"create config file for the new report"){

    fileFullName =  "${basedir}/grails-app/conf/metridoc/${rName}Config.groovy"
    if (argsMap['o'] || isFileOverwrite(fileFullName)) {
        ant.mkdir(dir: "${basedir}/grails-app/conf/metridoc")
        ant.touch(file: fileFullName)
        grailsConsole.info("Created ${fileFullName}")
    } else {
        grailsConsole.info("Ignored ${fileFullName}")
    }
}

/*grails-app/views/reports/foo/_foo.gsp(empty)
   *grails-app/views/reports/foo/_description.gsp(empty) */
target(createGSPFile:"create .gsp file for the new report"){
    ant.mkdir(dir: "${basedir}/grails-app/views/reports/${name}")
    fileFullName =  "${basedir}/grails-app/views/reports/${name}/_${name}.gsp"
    if (argsMap['o'] || isFileOverwrite(fileFullName)) {
        ant.touch(file: fileFullName)
        grailsConsole.info("Created ${fileFullName}")
    } else {
        grailsConsole.info("Ignored ${fileFullName}")
    }
    fileFullName =  "${basedir}/grails-app/views/reports/${name}/_description.gsp"
    if (argsMap['o'] || isFileOverwrite(fileFullName)) {
        ant.touch(file: fileFullName)
        grailsConsole.info("Created ${fileFullName}")
    } else {
        grailsConsole.info("Ignored ${fileFullName}")
    }
}

/*web-app/foo/css/foo.css(empty)
*web-app/foo/js/foo.js(empty) */
target(createStyleFile:"create .css .js file for the new report"){
    fileFullName =  "${basedir}/web-app/${name}/css/${name}.css"
    if (argsMap['o'] || isFileOverwrite(fileFullName)) {
        ant.mkdir(dir: "${basedir}/web-app/${name}/css")
        ant.touch(file: fileFullName)
        grailsConsole.info("Created ${fileFullName}")
    } else {
        grailsConsole.info("Ignored ${fileFullName}")
    }

    fileFullName = "${basedir}/web-app/${name}/js/${name}.js"
    if (argsMap['o'] || isFileOverwrite(fileFullName)) {
        ant.mkdir(dir: "${basedir}/web-app/${name}/js")
        ant.touch(file: fileFullName)
        grailsConsole.info("Created ${fileFullName}")
    } else {
        grailsConsole.info("Ignored ${fileFullName}")
    }
}

/*scripts/RunFoo.groovy(empty)*/
target(createRunScript:"create script to run new report"){

    fileFullName =  "${basedir}/scripts/Run${rName}.groovy"
    if (argsMap['o'] || isFileOverwrite(fileFullName)) {
        ant.mkdir(dir: "${basedir}/scripts")
        ant.touch(file: fileFullName)
        grailsConsole.info("Created ${fileFullName}")
    } else {
        grailsConsole.info("Ignored ${fileFullName}")
    }
}

/*grails-app/conf/FooResources.groovy*/
target(createResourceFile:"create Resource file for the new report"){
    def binding = [lowcaseName: "${name}", upcaseName: "${rName}"]
    fileFullName = "${basedir}/grails-app/conf/${rName}Resources.groovy"
    if (argsMap['o'] || isFileOverwrite(fileFullName)) {
        ant.mkdir(dir: "${basedir}/grails-app/conf")
        ant.touch(file: fileFullName)
        def textInResources = defineResources()
        def templateInResource = simpleEngine.createTemplate(textInResources).make(binding)
        new File(fileFullName).write(templateInResource.toString())
        grailsConsole.info("Created ${fileFullName}")
    } else {
        grailsConsole.info("Ignored ${fileFullName}")
    }

}

/*grails-app/controllers/metridoc/foo/FooController.groovy*/
target(createControllerFile:"create default controller file for the new report"){
    def binding = [lowcaseName: "${name}", upcaseName: "${rName}"]
    fileFullName = "${basedir}/grails-app/controllers/metridoc/${name}/${rName}Controller.groovy"
    if (argsMap['o'] || isFileOverwrite(fileFullName)) {
        ant.mkdir(dir: "${basedir}/grails-app/controllers/metridoc/${name}")
        ant.touch(file: fileFullName)
        def textInController = defineController()
        def templateInController = simpleEngine.createTemplate(textInController).make(binding)
        new File(fileFullName).write(templateInController.toString())
        grailsConsole.info("Created ${fileFullName}")
    } else {
        grailsConsole.info("Ignored ${fileFullName}")
    }
}

/*grails-app/services/metridoc/foo/FooService.groovy*/
target(createServiceFile: "Create default service file for the new report"){
    def binding = [lowcaseName: "${name}", upcaseName: "${rName}"]
    fileFullName =  "${basedir}/grails-app/services/metridoc/${name}/${rName}Service.groovy"
    if (argsMap['o'] || isFileOverwrite(fileFullName)) {
        ant.mkdir(dir: "${basedir}/grails-app/services/metridoc/${name}")
        ant.touch(file: fileFullName)
        def textInService = defineService()
        def templateInService = simpleEngine.createTemplate(textInService).make(binding)
        new File(fileFullName).write(templateInService.toString())
        grailsConsole.info("Created ${fileFullName}")
    } else {
        grailsConsole.info("Ignored ${fileFullName}")
    }
}

/*
*grails-app/conf/schemas/foo/foo.changelog-01.xml
*grails-app/conf/schemas/foo/fooSchema.xml
*/
target(createSchemaFile: "create default schema files for the new report"){
    def binding = [lowcaseName: "${name}", upcaseName: "${rName}"]
    ant.mkdir(dir: "${basedir}/grails-app/conf/schemas/${name}")
    fileFullName = "${basedir}/grails-app/conf/schemas/${name}/${name}.changelog-01.xml"
    if (argsMap['o'] || isFileOverwrite(fileFullName)) {
        ant.touch(file: fileFullName)
        def textChangeLog = defineChangeLog()
        def templateChangeLog = xmlEngine.createTemplate(textChangeLog).make(binding)
        new File(fileFullName).write(templateChangeLog.toString())
        grailsConsole.info("Created ${fileFullName}")
    } else {
        grailsConsole.info("Ignored ${fileFullName}")
    }

    fileFullName =  "${basedir}/grails-app/conf/schemas/${name}/${name}Schema.xml"
    if (argsMap['o'] || isFileOverwrite(fileFullName)) {
        ant.touch(file: fileFullName)
        def textSchema = defineSchema()
        def templateSchema = xmlEngine.createTemplate(textSchema).make(binding)
        new File(fileFullName).write(templateSchema.toString())
        grailsConsole.info("Created ${fileFullName}")
    } else {
        grailsConsole.info("Ignored ${fileFullName}")
    }

}

setDefaultTarget(main)
