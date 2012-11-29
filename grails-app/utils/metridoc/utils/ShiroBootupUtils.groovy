package metridoc.utils

import org.apache.commons.lang.StringUtils

/**
 * @author Tommy Barker
 *
 * Basic static class to handle some default parameters in the shiro plugin
 */
class ShiroBootupUtils {

    static final DEFAULT_MAP = [
                    
    ]
    
    static addDefaultParameters(ConfigObject configObject) {
        //if not set by the user in Config.groovy or elsewhere
        def filter = configObject.filter
        if (!filter.filterChainDefinitions) {
            filter.filterChainDefinitions = StringUtils.EMPTY
        }
        def currentFilterChainDefinitions = filter.filterChainDefinitions

        filter.filterChainDefinitions = currentFilterChainDefinitions + """

                /rest/*Admin/** = authcBasic,roles[ROLE_ADMIN]
                /rest/whoami = authcBasic
                /rest/admin/** = authcBasic,roles[ROLE_ADMIN]
                /rest/log/** = authcBasic,roles[ROLE_ADMIN]
                /rest/profile/** = authcBasic
                /rest/quartz/** = authcBasic,roles[ROLE_ADMIN]
                /rest/role/** = authcBasic,roles[ROLE_ADMIN]
                /rest/status/** = authcBasic,roles[ROLE_ADMIN]
                /rest/user/** = authcBasic,roles[ROLE_ADMIN]

                /*Admin/** = user,roles[ROLE_ADMIN]
                /whoami = user
                /admin/** = user,roles[ROLE_ADMIN]
                /log/** = user,roles[ROLE_ADMIN]
                /profile/** = user
                /quartz/** = user,roles[ROLE_ADMIN]
                /role/** = user,roles[ROLE_ADMIN]
                /status/** = user,roles[ROLE_ADMIN]
                /user/** = user,roles[ROLE_ADMIN]
                /logout = logout
            """
    }
}
