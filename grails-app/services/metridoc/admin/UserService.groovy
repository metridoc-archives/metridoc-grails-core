package metridoc.admin

import metridoc.reports.ShiroRole

class UserService {

    static final ROLE_ANONYMOUS = "ROLE_ANONYMOUS"

    def addRolesToUser(userInstance, roles) {

        def rolesToAdd = [ROLE_ANONYMOUS] as Set //there by default
        if(roles) {
            rolesToAdd.addAll roles
        }

        rolesToAdd.each {
            def role = ShiroRole.findByName(it)
            def instanceRoles = userInstance.roles
            if(instanceRoles == null) {
                userInstance.roles = [] as Set
                instanceRoles = userInstance.roles
            }
            instanceRoles.add(role)
        }
    }
}
