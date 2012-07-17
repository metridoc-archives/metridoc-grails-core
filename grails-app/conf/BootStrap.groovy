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

import metridoc.admin.ReportsConfiguration
import metridoc.reports.ShiroRole
import metridoc.reports.ShiroUser
import org.apache.shiro.crypto.hash.Sha256Hash
import org.codehaus.groovy.grails.commons.GrailsClass

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
class BootStrap {

    def grailsApplication
    final static DEFAULT_PASSWORD = "password"

    def init = { servletContext ->

        try {
            ShiroUser.withTransaction {

                def adminUser = ShiroUser.find {
                    username == "admin"
                }

                log.info "admin user is ${adminUser}"

                if (!adminUser) {

                    def preConfiguredPassword = grailsApplication.config.metridoc.admin.password
                    def password = preConfiguredPassword ? preConfiguredPassword : DEFAULT_PASSWORD

                    if (DEFAULT_PASSWORD == password) {
                        log.warn "Could not find user admin, creating a default one with password '${DEFAULT_PASSWORD}'.  Change this immediatelly"
                    }
                    adminUser = new ShiroUser(username: 'admin', passwordHash: new Sha256Hash(password).toHex())
                    adminUser.addToPermissions("*:*")
                    adminUser.save()
                }

                def anonymousUser = ShiroUser.find() {
                    username == "anonymous"
                }

                def anonymousRole = ShiroRole.find() {
                    name == "ROLE_ANONYMOUS"
                }

                if (anonymousRole) {
                    anonymousRole.delete(flush: true)
                }

                println "anonymous user is: ${anonymousUser}"
                if (anonymousUser) {
                    println "deleting anonymous user"
                    anonymousUser.delete(flush: true)
                }

                anonymousRole = new ShiroRole(name: "ROLE_ANONYMOUS")
                anonymousUser = new ShiroUser(
                    username: "anonymous",
                    passwordHash: new Sha256Hash("password").toHex(),
                    vetted: true,
                )
                anonymousUser.addToRoles(anonymousRole)

                anonymousUser.save()

                def makeApplicationAnonymous = {applicationName ->
                    log.info "marking application ${applicationName} as an anonymous application"
                    anonymousRole.addToPermissions("${applicationName}:*")
                    anonymousRole.save()
                }


                grailsApplication.config.metridoc.anonymous.applications.each {
                    makeApplicationAnonymous(it)
                }
            }
        } catch (Exception e) {
            e.printStackTrace()
        }

        addReportConfiguration()
    }

    def addReportConfiguration() {
        ReportsConfiguration.withTransaction {
            grailsApplication.controllerClasses.each {GrailsClass clazz ->

                def controllerName = clazz.getShortName()
                int indexOfController = controllerName.lastIndexOf("Controller")
                def reportName =
                    (controllerName.charAt(0).toLowerCase() as String) +
                        controllerName.substring(1, indexOfController)


                def configuration = ReportsConfiguration.find {
                    name == reportName
                }
                if (!configuration) {


                    def reportConfiguration = new ReportsConfiguration()
                    reportConfiguration.admin = true
                    reportConfiguration.anonymous = false
                    reportConfiguration.name = reportName

                    if (!reportConfiguration.save()) {
                        log.error("could not save ${reportConfiguration}")
                        reportConfiguration.errors.allErrors.each {
                            log.error it
                        }
                    }
                }
            }
        }
    }
}
