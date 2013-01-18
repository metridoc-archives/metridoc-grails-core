package metridoc.admin

import metridoc.reports.ShiroRole

class ReportPermissions {
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
}
