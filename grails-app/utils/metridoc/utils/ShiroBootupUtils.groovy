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
                /*Admin/* = authc,roles[ROLE_ADMIN]
                /admin/* = authc,roles[ROLE_ADMIN]
                /log/* = authc,roles[ROLE_ADMIN]
                /profile/* = authc
                /quartz/* = authc,roles[ROLE_ADMIN]
                /role/* = authc,roles[ROLE_ADMIN]
                /status/* = authc,roles[ROLE_ADMIN]
                /user/* = authc,roles[ROLE_ADMIN]
            """
        }
    }
}
