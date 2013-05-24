package metridoc.core

class ManageReport {
    /**
     * name of the controller to protect
     */
    String controllerName
    /**
     * must a user be logged in?
     */
    Boolean isProtected = false
    /**
     * what roles must a user have to access the controller
     */
    static hasMany = [roles: ShiroRole]

    static mapping = {
        controllerName(index: "idx_report_permissions_controller_name")
    }

    static constraints = {

    }

    static void overrideRoleMaps(Map<String, Map<String, List<String>>> roleMaps) {
        roleMaps.each {
            overrideRoleMap(it.key, it.value)
        }
    }

    static void overrideRoleMap(String controllerName, Map<String, List<String>> roleMap) {
        def permission = findByControllerName(controllerName)
        if (permission) {
            def roles = []
            roleMap.clear()
            roleMap << ["*": roles]
            permission.roles.each {
                roles << it.name
            }
        }
    }
}
