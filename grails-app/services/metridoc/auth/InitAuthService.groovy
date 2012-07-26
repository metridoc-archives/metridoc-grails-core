package metridoc.auth

import metridoc.reports.ShiroRole
import metridoc.reports.ShiroUser
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
    final static ADMIN = "admin"
    final static REPORT_USER = "report_user"
    final static ROLE = "ROLE_"
    final static DEFAULT_ROLES = [ANONYMOUS, ADMIN, REPORT_USER]

    def init() {
        initDefaultRoles()
        initAdminUser()
        initAnonymousUser()
    }

    def initAdminUser() {
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

                def adminRole = ShiroRole.find {
                    name == createRoleName(ADMIN)
                }
                adminUser.addToRoles(adminRole)
                adminUser.save()
            } else {
                log.info "admin user exists, the default admin does not need to be created"
            }
        }
    }

    def initAnonymousUser() {
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
                )

            }

            def hasRoles = anonymousUser.roles

            if (!hasRoles) {
                def anonymousRole = ShiroRole.find {
                    name == createRoleName(ANONYMOUS)
                }
                anonymousUser.addToRoles(anonymousRole)
                anonymousUser.save()
            }
        }
    }

    def initDefaultRoles() {
        DEFAULT_ROLES.each {shortRoleName ->
            def roleExists = ShiroRole.find {
                name == createRoleName(shortRoleName)
            }

            if (!roleExists) {
                createRole(shortRoleName).save()
            }
        }
    }

    static createRole(String type) {
        def role = new ShiroRole(
            name: createRoleName(type)
        )

        role.addToPermissions(type)
    }

    static createRoleName(String type) {
        ROLE + type.toUpperCase()
    }
}
