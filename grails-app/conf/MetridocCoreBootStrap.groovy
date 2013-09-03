import metridoc.core.LdapRoleMapping

class MetridocCoreBootStrap {

    def initAuthService
    def homeService

    def init = { servletContext ->
        initAuthService.init()
        homeService.addApplicationsAndCategories()
    }
    def destroy = {
    }
}
