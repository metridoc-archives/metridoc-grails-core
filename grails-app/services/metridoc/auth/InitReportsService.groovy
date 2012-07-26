package metridoc.auth

import metridoc.admin.ReportsConfiguration
import metridoc.utils.ClassUtils
import org.apache.commons.lang.StringUtils
import org.codehaus.groovy.grails.commons.GrailsClass

class InitReportsService {
    def grailsApplication
    static final ANONYMOUS_APPS = ["home", "logout", "auth"]

    def init() {
        ReportsConfiguration.withTransaction {
            grailsApplication.controllerClasses.each {GrailsClass grailsClass ->

                def clazz = grailsClass.clazz
                def artifactName = StringUtils.uncapitalise(grailsClass.name)
                def reportName = ClassUtils.getStaticVariable(clazz, "reportName", artifactName)

                def configuration = ReportsConfiguration.find {
                    name == artifactName
                }

                if (!configuration) {

                    def isAdmin = ClassUtils.getStaticVariable(clazz, "adminOnly", false)
                    def role = isAdmin ? "ROLE_ADMIN" : "ROLE_REPORT_USER"

                    if (ANONYMOUS_APPS.contains(artifactName)) {
                        role = "ROLE_ANONYMOUS"
                    }

                    def reportConfiguration = new ReportsConfiguration()
                    reportConfiguration.role = role
                    reportConfiguration.name = artifactName
                    reportConfiguration.displayName = reportName

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
