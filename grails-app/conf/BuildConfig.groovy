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

grails.servlet.version = "2.5" // Change depending on target container compliance (2.5 or 3.0)
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.target.level = 1.6
grails.project.source.level = 1.6
//grails.project.war.file = "target/${appName}-${appVersion}.war"

//location of the release repository
grails.project.repos.metridocRepo.url = new File("../maven/repository").toURI().toURL().toString()
//name of the repository
grails.project.repos.default = "metridocRepo"

codenarc.properties = {
    // Each property definition is of the form:  RULE.PROPERTY-NAME = PROPERTY-VALUE
    GrailsPublicControllerMethod.enabled = false
}

grails.project.dependency.resolution = {
    // inherit Grails' default dependencies, this has to be here
    inherits("global")
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    checksums true // Whether to verify checksums on resolve

    repositories {
        inherits true // Whether to inherit repository definitions from plugins
        grailsPlugins()
        grailsHome()
        grailsCentral()
        mavenCentral()
        mavenRepo "https://oss.sonatype.org/content/repositories/snapshots"
    }

    //standard jar maven dependencies
    dependencies {
        //used for testing camel routes
        test("org.apache.camel:camel-test:2.9.2")
        //by default metridoc is tested on mysql
        compile("mysql:mysql-connector-java:5.1.20")
        //used for handling xls files
        compile("org.apache.poi:poi:3.8-beta3")
        compile("org.apache.poi:poi-ooxml:3.8-beta3")
        //used for handling csv easily
        compile 'net.sf.opencsv:opencsv:2.3'
        compile('org.apache.camel:camel-core:2.9.2') {
            excludes "slf4j-api"
        }
        compile('org.apache.camel:camel-groovy:2.9.2') {
            excludes 'spring-context', 'spring-aop', 'spring-tx', 'groovy-all'
        }
        compile('org.apache.camel:camel-stream:2.9.2')
        compile('org.apache.camel:camel-ftp:2.9.2')
    }

    //grails based plugins
    plugins {
        runtime ":jquery:1.8.0"
        compile ":plugin-config:0.1.5"
        compile ":quartz2:0.2.3"
        runtime ":mail:1.0.1"
        runtime ":hibernate:$grailsVersion"
        runtime ":resources:1.1.6"
        runtime(":shiro:1.1.4") {
            excludes(
                    [name: "shiro-quartz", group: "org.apache.shiro"]
            )
        }
        runtime ":jquery-ui:1.8.24"
        runtime ":webxml:1.4.1"
        build ":tomcat:$grailsVersion"
        build ":rest-client-builder:1.0.2"
        build ":release:$grailsVersion"
        build ":svn:1.0.2"
        build(":codenarc:0.18") {
            excludes "log4j", "groovy-all", "ant"
        }
    }
}
