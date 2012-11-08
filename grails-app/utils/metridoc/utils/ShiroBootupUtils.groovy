package metridoc.utils

/**
 * @author Tommy Barker
 *
 * Basic static class to handle some default parameters in the shiro plugin
 */
class ShiroBootupUtils {

    static addDefaultParameters(ConfigObject configObject) {
        //if not set by the user in Config.groovy or elsewhere
        def filter = configObject.filter
        if(!filter.filterChainDefinitions) {
            filter.filterChainDefinitions = """
                /*Admin/* = user,roles[ROLE_ADMIN]
                /whoami = authcBasic
                /admin/* = user,roles[ROLE_ADMIN]
                /log/* = user,roles[ROLE_ADMIN]
                /profile/* = user
                /quartz/runNow = authcBasic,roles[ROLE_ADMIN]
                /quartz/* = user,roles[ROLE_ADMIN]
                /role/* = user,roles[ROLE_ADMIN]
                /status/* = user,roles[ROLE_ADMIN]
                /user/* = user,roles[ROLE_ADMIN]
            """
        }
    }
}
