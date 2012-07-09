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

includeTargets << grailsScript("_GrailsInit")

target(main: "create default fileset for user customised report") {
    String name = argsMap["params"][0]
    name.toLowerCase()
    StringBuilder rName = new StringBuilder(name.size())
    rName.append(Character.toUpperCase(name.charAt(0)))
    rName.append(name.substring(1))

    /*grails-app/conf/metridoc/FooConfig.groovy (empty)*/
    ant.mkdir(dir: "${basedir}/grails-app/conf/metridoc")
    ant.touch(file: "${basedir}/grails-app/conf/metridoc/${rName}Config.groovy")

    /*grails-app/views/reports/foo/reports/_foo.gsp(empty)  */
    ant.mkdir(dir: "${basedir}/grails-app/views/reports/${name}")
    ant.touch(file: "${basedir}/grails-app/views/reports/${name}/_${name}.gsp")

    /*scripts/RunFoo.groovy(empty)*/
    ant.mkdir(dir: "${basedir}/scripts")
    ant.touch(file: "${basedir}/scripts/Run${rName}.groovy")

    /*grails-app/conf/schemas/foo/foo.changelog-01.xml
     *grails-app/conf/schemas/foo/fooSchema.xml*/
    ant.mkdir(dir: "${basedir}/grails-app/conf/schemas/${name}")
    ant.touch(file: "${basedir}/grails-app/conf/schemas/${name}/${name}.changelog-01.xml")
    ant.touch(file: "${basedir}/grails-app/conf/schemas/${name}/${name}Schema.xml")
    def binding = [lowcaseName: "${name}", upcaseName: "${rName}"]
    def textChangeLog = '''<databaseChangeLog xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
                   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                   xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-2.0.xsd">
                   </databaseChangeLog>'''
    def textSchema = '''<databaseChangeLog xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
                   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                   xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-2.0.xsd">
                   <include file="schemas/\${lowcaseName}/\${lowcaseName}.changelog-01.xml"/>
                   </databaseChangeLog>'''
    def xmlEngine = new groovy.text.XmlTemplateEngine()
    def templateChangeLog = xmlEngine.createTemplate(textChangeLog).make(binding)
    def templateSchema = xmlEngine.createTemplate(textSchema).make(binding)
    new File("${basedir}/grails-app/conf/schemas/${name}/${name}Schema.xml").write(templateSchema.toString())
    new File("${basedir}/grails-app/conf/schemas/${name}/${name}.changelog-01.xml").write(templateChangeLog.toString())

    /*grails-app/conf/FooResources.groovy*/
    ant.mkdir(dir: "${basedir}/grails-app/conf")
    ant.touch(file: "${basedir}/grails-app/conf/${rName}Resources.groovy")
    def textInResources = '''modules = {
    \${lowcaseName} {
        dependsOn 'jquery-ui'
        resource url: '\${lowcaseName}/css/\${lowcaseName}.css'
        resource url: '\${lowcaseName}/js/\${lowcaseName}.js'
    }
}'''
    def simpleEngine = new groovy.text.SimpleTemplateEngine()
    def templateInResource = simpleEngine.createTemplate(textInResources).make(binding)
    new File("${basedir}/grails-app/conf/${rName}Resources.groovy").write(templateInResource.toString())

    /*
    *grails-app/controllers/metridoc/foo/FooController.groovy
    */
    ant.mkdir(dir: "${basedir}/grails-app/controllers/metridoc/${name}")
    ant.touch(file: "${basedir}/grails-app/controllers/metridoc/${name}/${rName}Controller.groovy")
    def textInController = '''package metridoc.\${lowcaseName}

            class \${upcaseName}Controller {

                def index() { }
            }'''
    def templateInController = simpleEngine.createTemplate(textInController).make(binding)
    new File("${basedir}/grails-app/controllers/metridoc/${name}/${rName}Controller.groovy").write(templateInController.toString())
    /*
     * grails-app/services/metridoc/foo/FooService.groovy
     */
    ant.mkdir(dir: "${basedir}/grails-app/services/metridoc/${name}")
    ant.touch(file: "${basedir}/grails-app/services/metridoc/${name}/${rName}Service.groovy")
    def textInService = '''package metridoc.\${lowcaseName}

            class \${upcaseName}Service {

                def serviceMethod() {

                }
            }'''
    def templateInService = simpleEngine.createTemplate(textInService).make(binding)
    new File("${basedir}/grails-app/services/metridoc/${name}/${rName}Service.groovy").write(templateInService.toString())

}

setDefaultTarget(main)
