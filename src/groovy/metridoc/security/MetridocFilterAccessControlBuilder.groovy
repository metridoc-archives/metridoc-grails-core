package metridoc.security

import org.apache.shiro.grails.FilterAccessControlBuilder
import org.apache.shiro.subject.Subject
import org.apache.shiro.SecurityUtils

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 8/13/12
 * Time: 3:46 PM
 */
class MetridocFilterAccessControlBuilder extends FilterAccessControlBuilder{

    def request
    def response
    def session
    def servletContext
    def flash
    def params
    def actionName
    def controllerName
    def grailsApplication
    def applicationContext

    MetridocFilterAccessControlBuilder(Subject subject) {
        super(subject)
    }

    MetridocFilterAccessControlBuilder() {
        this(SecurityUtils.subject)
    }
}