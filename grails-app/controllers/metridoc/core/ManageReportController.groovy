package metridoc.core

import grails.web.RequestParameter
import metridoc.admin.ManageReport
import metridoc.reports.ShiroRole

class ManageReportController {

    def manageReportService
    def initAuthService
    def grailsApplication

    static accessControl = {
        role(name: "ROLE_ADMIN")
    }

    def index() {
        [
                controllerDetails: manageReportService.controllerDetails,
                shiroFilters: grailsApplication.config.security.shiro.filter.filterChainDefinitions
        ]
    }

    def show(@RequestParameter('id') String controllerName) {
        if (controllerName == null || !controllerName.trim()) {
            render(status: 404, text: "A controller name is required")
            return
        }
        def controllerDetails = manageReportService.controllerDetails[controllerName]
        if (controllerDetails == null) {
            render(status: 404, text: "Could not find ${controllerName}")
            return
        }

        [controllerDetails: controllerDetails]
    }

    def update(String controllerName) {

        def roles = params.roles
        boolean isProtected = params.isProtected ? true : false
        if (roles == null) {
            roles = []
        }

        if (roles instanceof String) {
            roles = [roles]
        }

        if (controllerName) {
            def report = ManageReport.findByControllerName(controllerName)
            if (report == null) {
                report = new ManageReport(controllerName: controllerName)
            }

            report.roles.clear()
            if (roles) {
                report.isProtected = true
            } else {
                report.isProtected = isProtected
            }

            roles.each { roleName ->
                def role = ShiroRole.findByName(roleName)
                report.addToRoles(role)
            }

            if (report.save(flush: true)) {
                flash.info = "updated security for controller [$controllerName]"
            } else {
                log.error report.errors
                flash.alert = "errors occurred trying to save $controllerName"
            }
            //make changes to the role map
            initAuthService.init()
        }
        redirect(action: "index")
    }
}


