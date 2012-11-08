package metridoc.core

import org.apache.commons.lang.StringUtils
import org.codehaus.groovy.grails.commons.GrailsClassUtils

class HomeService {

    static final HOME_DATA_FIELD = "homePage"
    def grailsApplication

    /**
     *
     * @return indication whether or not a controller is an application controller.  These include all controllers
     * that have no <code>homePage</code> variable, or have a <code>homePage</code> variable but do not have the
     * <code>adminOnly</code> variable set to true
     *
     */
    def getApplicationControllers() {
        return getControllerMetaData {
            def isNull = it == null
            def notAdmin = isNull ? true : !it.adminOnly
            def shouldInclude = isNull ? true : !it.exclude

            return isNull || (notAdmin && shouldInclude)
        }
    }

    /**
     * returns all administrative controllers
     */
    def getAdminControllers() {
        return getControllerMetaData {
            return it != null && it.adminOnly
        }
    }

    /**
     *
     * @param closure indicates whether the controller information should be collected or not
     */
    def getControllerMetaData(Closure closure) {
        def result = [:] as TreeMap
        def securityConfig = grailsApplication.mergedConfig.metridoc.homePage
        grailsApplication.controllerClasses.each {controller ->
            def dataField = GrailsClassUtils.getStaticFieldValue(controller.clazz, HOME_DATA_FIELD)
            def homePageMetaData = dataField ? dataField : [:]
            def uncapControllerName = StringUtils.uncapitalise(controller.name)
            def homePageInfoFromConfig = securityConfig[uncapControllerName] ? securityConfig[uncapControllerName] : [:]
            homePageMetaData.putAll(homePageInfoFromConfig)
            if (closure.call(homePageMetaData)) {
                if(!homePageMetaData || !homePageMetaData.title) {
                    homePageMetaData.title = controller.naturalName
                }
                homePageMetaData.put("controllerName", controller.name)
                def title = homePageMetaData.title
                result.put(title, homePageMetaData)
            }
        }

        return result.values()
    }
}
