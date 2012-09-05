package metridoc.core

import metridoc.security.MetridocFilterAccessControlBuilder
import org.apache.commons.lang.StringUtils
import org.apache.shiro.grails.FilterAccessControlBuilder
import metridoc.security.MetridocFilterAccessControlBuilder

class SecurityService {

    def grailsApplication
    private initiated = false
    def anonymousApps
    def fallback
    def customSecurityByController
    static transactional = false

    def initiate() {
        def security = grailsApplication.config.metridoc.security
        anonymousApps = security.anonymous
        fallback = security.fallback
        customSecurityByController = [:]
        if (security.custom) {
            security.custom.each {
                customSecurityByController.put(it.key, it.value)
            }
        }

        initiated = true
    }

    def authorized(LinkedHashMap delegate, controllerName) {
        if (!initiated) {
            initiate()
        }

        return authorized(new MetridocFilterAccessControlBuilder(delegate), controllerName)
    }

    private authorized(FilterAccessControlBuilder delegate, controllerName) {
        if (delegate.role("ROLE_ADMIN")) return true

        def closure = customSecurityByController[controllerName]
        if(closure) {
            return runClosure(delegate, closure)
        }

        if (anonymousApps.contains(controllerName)) return true

        return runClosure(delegate, fallback)
    }

    private runClosure(delegate, Closure closure) {
        closure.delegate = delegate
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        return closure.call()
    }
}
