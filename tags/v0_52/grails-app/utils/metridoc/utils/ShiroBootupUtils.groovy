package metridoc.utils

import org.apache.commons.lang.StringUtils
import org.apache.commons.lang.text.StrBuilder

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

        filter.filterChainDefinitions = addRestDefinitions(filter.filterChainDefinitions)
    }

    static addRestDefinitions(String currentConfiguration) {
        StrBuilder builder = new StrBuilder()
        builder.appendln(currentConfiguration)

        currentConfiguration.eachLine {
            def prefix = "/rest"
            def trimmed = it.trim()
            if(trimmed) {
                def currentLine = "${prefix}${trimmed}"
                currentLine = currentLine.replaceAll("authc", "authcBasic")
                currentLine = currentLine.replaceAll("user", "authcBasic")
                if(!currentLine.contains("authcBasic")) {
                    currentLine = currentLine.replaceAll("=", "= authcBasic,")
                }

                builder.appendln(currentLine)
            }
        }

        return builder.toString()
    }
}
