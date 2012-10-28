package metridoc.core

import org.apache.commons.lang.StringUtils
import org.codehaus.groovy.grails.commons.GrailsClassUtils

class HomeService {


    def grailsApplication

    def getLayoutConfig() {
        grailsApplication.config.metridoc.style.home.layout ? grailsApplication.config.metridoc.style.home.layout : 'main'
    }

    def getControllerInformation() {
        def result = []
        grailsApplication.controllerClasses.each {controller ->
            def homeLinks = GrailsClassUtils.getStaticFieldValue(controller.clazz, "home")
            if (homeLinks) {
                homeLinks.each {homeLink ->
                    homeLink << [controllerName: controller.name]
                    result << homeLink
                }
            }
        }

        return result
    }


}
