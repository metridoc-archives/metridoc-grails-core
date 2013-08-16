package metridoc.core

import org.apache.shiro.SecurityUtils

class ManageAccessController {

    static homePage = [
            title: "Manage Metridoc",
            adminOnly: true,
            description: """
                Create, update and delete users and roles.  Change configuration, load plugins and restart the
                application
            """
    ]

    static accessControl = {
        role(name: "ROLE_ADMIN")
    }

    def manageReportService

    def index() {
        chain(action: "list")
    }

    def list() {
        def max = Math.min(params.max ? params.int('max') : 10, 100)
        params.max = max
        def userCount = ShiroUser.count()
        def showUserPagination = userCount > max
        def roleCount = ShiroRole.count()
        def showRolePagination = roleCount > max
        def previousExpanded = ""
        if (params.previousExpanded != 'clear') {
            previousExpanded = session.getAttribute("previousExpanded")
        } else {
            previousExpanded = "none"
        }
        session.setAttribute("previousExpanded", "none")
        def oldSearch = session.getAttribute("searchFilter")
        session.setAttribute("searchFilter", "")
        [
                previousExpanded: previousExpanded,
                currentUserName: SecurityUtils.getSubject().getPrincipal(),
                shiroUserInstanceList: ShiroUser.list(params),
                shiroUserInstanceTotal: userCount,
                showUserPagination: showUserPagination,
                shiroRoleInstanceList: ShiroRole.list(params),
                shiroRoleInstanceTotal: roleCount,
                showRolePagination: showRolePagination,
                controllerDetails: manageReportService.controllerDetails,
                shiroFilters: grailsApplication.config.security.shiro.filter.filterChainDefinitions,
                searchFilter: oldSearch
        ]
    }
}
