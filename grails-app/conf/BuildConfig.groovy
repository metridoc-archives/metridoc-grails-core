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

grails.servlet.version = "2.5" // Change opendepending on target container compliance (2.5 or 3.0)
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.target.level = 1.7
grails.project.source.level = 1.7
//grails.project.war.file = "target/${appName}-${appVersion}.war"

//location of the release repository
grails.project.repos.metridocRepo.url = "https://dl.bintray.com/upennlib/metridoc"
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
        mavenLocal()
        grailsPlugins()
        grailsHome()
        grailsCentral()
        mavenCentral()
        mavenRepo "https://oss.sonatype.org/content/repositories/snapshots"
        mavenRepo "http://dl.bintray.com/upennlib/metridoc"
        mavenRepo "http://jcenter.bintray.com"
    }

    //standard jar maven dependencies
    dependencies {
        //TEMPLATE_METRIDOC_JOB_CORE
        compile("com.googlecode.metridoc:metridoc-job-core:0.5.RC4")
        //TEMPLATE_METRIDOC_JOB_CORE
        //used for testing camel routes
        test("org.apache.camel:camel-test:2.11.0")
        //by default metridoc is tested on mysql
        compile("mysql:mysql-connector-java:5.1.20")
        compile('org.jasypt:jasypt:1.9.0')
    }

    //grails based plugins
    plugins {
        compile(":job-runner:0.4")
        compile ":external-config-reload:1.2.2"
        compile ":plugin-config:0.1.5"
        runtime ":mail:1.0.1"
        runtime ":hibernate:$grailsVersion"
        runtime ":resources:1.1.6"
        runtime(":shiro:1.1.4") {
            excludes(
                    [name: "shiro-quartz", group: "org.apache.shiro"]
            )
        }
        build(":tomcat:$grailsVersion") {
            export = false
        }
        compile(":rest-client-builder:1.0.3")
        build(":release:2.2.1") {
            export = false
        }
        build(":codenarc:0.18") {
            excludes "log4j", "groovy-all", "ant", "junit"
            export = false
        }

        test(":code-coverage:1.2.5") {
            export = false
        }
    }
}
