package metridoc.core

class LdapRoleMapping {

    String name

    static hasMany = [roles: ShiroRole]
    static constraints = {
        name(blank: false, unique: true)
    }
}
