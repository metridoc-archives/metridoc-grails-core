package metridoc.auth

import metridoc.reports.ShiroUser
import org.apache.commons.lang.exception.ExceptionUtils
import org.apache.shiro.crypto.hash.Sha256Hash

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
    final static ANONYMOUS = "anonymous"

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


                if (!adminUser) {

                    def preConfiguredPassword = grailsApplication.config.metridoc.admin.password
                    def password = preConfiguredPassword ? preConfiguredPassword : DEFAULT_PASSWORD

                    if (DEFAULT_PASSWORD == password) {
                        log.warn "Could not find user admin, creating a default one with password '${DEFAULT_PASSWORD}'.  Change this immediatelly"
                    }
                    adminUser = new ShiroUser(username: 'admin', passwordHash: new Sha256Hash(password).toHex())
                    adminUser.addToPermissions("*:*")
                    adminUser.save()
                } else {
                    log.info "admin user exists, the default admin does not need to be created"
                }
            }
        } catch (Exception e) {
            log.error "Exception occurred trying to ", e
        }
    }

    def initAnonymousUser() {
        try {
            ShiroUser.withTransaction {
                def anonymousUser = ShiroUser.find() {
                    username == ANONYMOUS
                }

                if (anonymousUser) {
                    log.info "anonymous user found, don't need to create a default one"

                } else {
                    anonymousUser = new ShiroUser(
                        username: "anonymous",
                        passwordHash: new Sha256Hash("password").toHex(),
                        vetted: true,
                    )
                    anonymousUser.addToPermissions("anonymous")
                }

                def anonymousPermissionFound = anonymousUser.permissions.contains(ANONYMOUS)

                if (!anonymousPermissionFound) {
                    anonymousUser.addToPermissions(ANONYMOUS)
                }
                anonymousUser.save()
            }
        } catch (Exception e) {
            log.error "Exception occurred trying to ", e
        }
    }
}
