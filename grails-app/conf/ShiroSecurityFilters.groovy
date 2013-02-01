import org.apache.shiro.SecurityUtils
import org.codehaus.groovy.grails.commons.GrailsClass

/**
 * Generated by the Shiro plugin. This filters class protects all URLs
 * via access control by convention.
 */
class ShiroSecurityFilters {
    def manageReportService

    def filters = {
        all(uri: "/**") {
            before = {
                // Ignore direct views (e.g. the default main index page).
                if (!controllerName) return true
                //this is handled by shiro's filter map
                if (request.requestURL.contains("/rest/")) return true

                def details = manageReportService.getControllerDetails().get(controllerName)
                def roles = details.roles
                if (roles) {
                    accessControl(auth: false) {
                        def hasAccess = true
                        roles.each {
                            log.debug "checking for access to $controllerName for role ${it}"
                            hasAccess = hasAccess && role(it)
                        }

                        return hasAccess
                    }
                }

                def isProtected = details.isProtected
                if (isProtected) {
                    def notLoggedIn = SecurityUtils.subject.principal == null
                    if (notLoggedIn) {
                        //will force a login
                        accessControl()
                    }
                }
            }
        }
    }
}
