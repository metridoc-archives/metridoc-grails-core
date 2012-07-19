package metridoc.auth

import metridoc.admin.ReportsConfiguration
import org.codehaus.groovy.grails.commons.GrailsClass

class InitReportsService {
    def grailsApplication

    def init() {
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
