import metridoc.core.LdapRoleMapping

class MetridocCoreBootStrap {

    def initAuthService
    def homeService

    def init = { servletContext ->
        initAuthService.init()
        homeService.addApplicationsAndCategories()

        //Make dummy LdapRoleMappings for testing
        def adminGroup = new LdapRoleMapping(name: "Administration")
        adminGroup.save()
        def userGroup = new LdapRoleMapping(name: "Users")
        userGroup.save()
    }

    def destroy = {
    }
}
