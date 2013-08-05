package metridoc.core

class DeleteTempLdapFilters {

    def filters = {
        notLdap(controller: 'AdminLdap', invert: true) {
            before = {
                def tempLdapConfig = LdapData.findByName("temp")
                if (tempLdapConfig) tempLdapConfig.delete()
            }

        }
    }
}