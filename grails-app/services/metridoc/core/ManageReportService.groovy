package metridoc.core

import metridoc.admin.ManageReport
import org.codehaus.groovy.grails.commons.GrailsClass
import org.codehaus.groovy.grails.commons.GrailsControllerClass

class ManageReportService {

    def pluginManager
    def grailsApplication

    Map getRoleMaps() {
        pluginManager.getGrailsPluginForClassName("ShiroGrailsPlugin").instance.roleMaps
    }

    Set<String> getRolesByController(String controllerName) {
        getRolesByControllerAndRoleMap(controllerName, roleMaps)
    }

    static Set<String> getRolesByControllerAndRoleMap(String controllerName, Map roleMaps) {
        def normRoleMap = getNormalizedRoleMapsHelper(roleMaps)
        normRoleMap[controllerName] ?: [] as Set
    }

    boolean isProtected(String controllerName) {
        def reportPermission = ManageReport.findByControllerName(controllerName)

        if (reportPermission) {
            if (reportPermission.roles) {
                return true
            }
        }

        if (getRolesByController(controllerName)) {
            return true
        }
        def controller = grailsApplication.getArtefactByLogicalPropertyName("Controller", controllerName)
        return isControllerGrailsClassProtected(controller)
    }

    static boolean isControllerGrailsClassProtected(GrailsControllerClass clazz) {
        if (clazz.hasProperty("isProtected")) {
            return clazz.getPropertyValue("isProtected", Boolean)
        }

        return false
    }

    Map<String, Map<String, Set<String>>> getControllerDetails() {
        def result = [:]
        grailsApplication.controllerClasses.each {
            def controllerName = it.logicalPropertyName
            def controllerResult = [:]
            controllerResult.roles = getRolesByController(controllerName)
            controllerResult.isProtected = isProtected(controllerName)
            result[controllerName] = controllerResult
        }

        return result
    }

    static Map getNormalizedRoleMapsHelper(Map roleMaps) {

        def result = [:]

        roleMaps.each { String controllerName, Map roleMap ->
            def roles = [] as Set
            roleMap.each {
                roles.addAll(it.value)
            }
            result[controllerName] = roles
        }

        return result
    }
}
