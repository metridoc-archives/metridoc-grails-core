package metridoc.core

import org.codehaus.groovy.grails.commons.GrailsClassUtils

class AccessInfoService {

    def grailsApplication

    def buildHomeLinks() {

        def result = []
        grailsApplication.controllerClasses.each {controller ->
            def homeInfo = GrailsClassUtils.getStaticFieldValue(controller.clazz, "home")
            if (homeInfo) {
                homeInfo.each {
                    result << [controller: controller.name, action: it.action]
                }
            }
        }

        return result
    }
}
