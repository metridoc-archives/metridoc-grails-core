package metridoc.core

import org.apache.commons.lang.StringUtils

class HomeService {

    private _availableControllers
    def grailsApplication
    def securityService

    def getAvailableControllers() {
        if (_availableControllers) return _availableControllers

        _availableControllers = []
        grailsApplication.controllerClasses.each {
            _availableControllers << StringUtils.uncapitalize(it.name)
        }
    }

    def getLayoutConfig() {
        grailsApplication.config.metridoc.style.home.layout
    }

    def getAvailableApplications(homeController) {
        def applications = layoutConfig.availableApplications
        return doGetAppsIfAuthorized(applications, homeController)
    }

    def getAdminApps(homeController) {
        def applications = layoutConfig.administration
        return doGetAppsIfAuthorized(applications, homeController)
    }

    def doGetAppsIfAuthorized(applications, homeController) {

        def result = []
        applications.each {controllerName, reportName ->
            def availableControllers = getAvailableControllers()
            if (availableControllers.contains(controllerName)) {
                def delegate = [
                    request: homeController.request,
                    response: homeController.response,
                    session: homeController.session,
                    servletContext: homeController.servletContext,
                    flash: homeController.flash,
                    params: homeController.params,
                    actionName: homeController.actionName,
                    controllerName: homeController.controllerName,
                    grailsApplication: homeController.grailsApplication,
                    applicationContext: homeController.applicationContext
                ]

                if (securityService.authorized(delegate, controllerName)) {
                    result << [
                        reportName: reportName,
                        controllerName: controllerName
                    ]
                }
            }
        }

        return result
    }
}
