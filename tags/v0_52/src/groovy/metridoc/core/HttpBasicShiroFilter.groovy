package metridoc.core

import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter
import org.apache.shiro.web.util.WebUtils

import javax.servlet.ServletRequest

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 11/24/12
 * Time: 4:13 PM
 * To change this template use File | Settings | File Templates.
 */
class HttpBasicShiroFilter extends BasicHttpAuthenticationFilter {

    private final static REMEMBER_ME = "rememberMe"

    @Override
    protected boolean isRememberMe(ServletRequest request) {
        def isRememberMe = WebUtils.isTrue(request, REMEMBER_ME);
        return isRememberMe
    }
}
