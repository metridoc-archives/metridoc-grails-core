package metridoc.auth

import metridoc.reports.ShiroUser
import metridoc.reports.ShiroRole
import org.apache.shiro.crypto.hash.Sha256Hash
import org.apache.commons.lang.exception.ExceptionUtils

/**
 * Created with IntelliJ IDEA.
 * User: weizhuowu
 * Date: 7/17/12
 * Time: 5:33 PM
 * To change this template use File | Settings | File Templates.
 */
class InitAuthService {

    def grailsApplication
    final static DEFAULT_PASSWORD = "password"

    def init() {
        initAdminUser()
        initAnonymousUser()
    }

    def initAdminUser() {
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
            }
        } catch (Exception e) {
            log.info ExceptionUtils.getStackTrace(e)
            e.printStackTrace()
        }
    }

    def initAnonymousUser() {
        try {
            ShiroUser.withTransaction {
                def anonymousUser = ShiroUser.find() {
                    username == "anonymous"
                }
                def anonymousRole = ShiroRole.find() {
                    name == "ROLE_ANONYMOUS"
                }
                if (anonymousRole) {
                    anonymousRole.delete(flush: true)
                }
                log.info "anonymous user is: ${anonymousUser}"
                if (anonymousUser) {
                    log.info "deleting anonymous user"
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
            log.info ExceptionUtils.getStackTrace(e)
            e.printStackTrace()
        }
    }
}
