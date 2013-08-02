package metridoc.core

class LDAP_Data {
    String name = "LDAP_Config"
    String server = "default"
    String rootDN = "default"
    String userSearchBase = "default"
    String userSearchFilter = "default"
    String groupSearchBase = ""
    String managerDN = "default"
    String managerPassword

    static constraints = {
        server(nullable: false, blank: false)
        rootDN(nullable: false, blank: false)
        userSearchBase(nullable: false, blank: false)
        userSearchFilter(nullable: false, blank: false)
        managerDN(nullable: false, blank: false)
        managerPassword(nullable: false, blank: false)

    }
}
