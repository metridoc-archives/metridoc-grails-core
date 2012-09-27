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
grails.project.repos.metridocRepo.url = "https://metridoc.googlecode.com/svn/plugins"
grails.project.repos.default = "metridocRepo"

grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        excludes "groovy-1.7-rc-2"
    }
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

    dependencies {
        compile 'org.codehaus.gpars:gpars:0.12'
        compile("org.liquibase:liquibase-core:2.0.1")
        test("org.apache.camel:camel-test:2.9.2")
        compile("org.apache.camel:camel-ftp:2.9.2") {
            excludes "slf4j-api"
        }
        compile("org.grails:grails-scripts:${grailsVersion}")
        compile("org.apache.ivy:ivy:2.2.0")
        compile("mysql:mysql-connector-java:5.1.20")
        compile("org.codehaus.gant:gant_groovy1.8:1.9.8")
        compile("org.apache.poi:poi:3.8-beta3")
        compile("org.apache.poi:poi-ooxml:3.8-beta3") {
            excludes "xmlbeans"
        }
        compile("com.googlecode.gant-ext:gant-ext:0.5") {
            excludes 'logback-classic'
            excludes 'slf4j-api'
            excludes 'gant_groovy1.8'
        }
        compile("com.google.visualization:visualization-datasource:1.1.1") {
            excludes "commons-lang"
            excludes "commons-logging"
            excludes "opencsv"
        }
    }

    plugins {
        compile 'net.sf.opencsv:opencsv:2.3'
        compile ":plugin-config:0.1.5"
        compile ":plugin-config:0.1.5"
        compile ":quartz2:0.2.3"
        compile ":mail:1.0"
        compile (":routing:1.2.2") {
            excludes 'slf4j-api'
        }
        runtime ":hibernate:$grailsVersion"
        runtime ":jquery:1.7.1"
        runtime ":resources:1.1.6"
        runtime (":shiro:1.1.3") {
            excludes "shiro-quartz"
        }
        compile ":jquery-ui:1.8.15"
        runtime ":webxml:1.4.1"
        build ":tomcat:$grailsVersion"
        build ":rest-client-builder:1.0.2"
        build ":release:2.0.3"
        build ":svn:1.0.2"
    }
}
