package metridoc.core

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
        (normRoleMap[controllerName] ?: []) as Set<String>
    }

    boolean isProtected(String controllerName) {
        def reportPermission = ManageReport.findByControllerName(controllerName)

        if (reportPermission) {
            if (reportPermission.roles) {
                return true
            }
            if (reportPermission.isProtected) {
                return true
            }
        }

        if (getRolesByController(controllerName)) {
            return true
        }
        def controller = grailsApplication.getArtefactByLogicalPropertyName("Controller", controllerName)
        return isControllerGrailsClassProtected(controller as GrailsControllerClass)
    }

    static boolean isControllerGrailsClassProtected(GrailsControllerClass clazz) {
        if (clazz.hasProperty("isProtected")) {
            return clazz.getPropertyValue("isProtected", Boolean)
        }

        return false
    }

    Map<String, Map<String, Object>> getControllerDetails() {
        def result = [:]
        grailsApplication.controllerClasses.each {
            def controllerName = it.logicalPropertyName
            def controllerResult = [:]
            controllerResult.roles = getRolesByController(controllerName)
            controllerResult.isProtected = isProtected(controllerName)
            controllerResult.controllerName = controllerName
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
