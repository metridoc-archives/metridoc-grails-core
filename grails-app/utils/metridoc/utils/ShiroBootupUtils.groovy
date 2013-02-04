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
        def currentFilterChainDefinitions = filter.filterChainDefinitions.trim()

        if (!currentFilterChainDefinitions) {
            filter.filterChainDefinitions = """
/admin/** = user,roles[ROLE_ADMIN]
/rest/** = authcBasic, roles[ROLE_REST]"""
        }

    }
}
