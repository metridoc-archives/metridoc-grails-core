package metridoc.core

import org.apache.shiro.SecurityUtils
import org.apache.shiro.authc.AuthenticationException
import org.apache.shiro.crypto.hash.Sha256Hash

class RestController {

    def restService
    static final DEFAULT_ACTION = "index"

    def index() {
        def controllerForward = params.controllerForward
        def actionForward = params.actionForward ?: DEFAULT_ACTION

        def authHeader = request.getHeader("authorization")
        if (!authHeader) {
            response.addHeader("WWW-Authenticate", "Basic realm=\"metridoc-core\"")
            response.sendError(401)
            return
        }
        def userPWBytes = authHeader.replace("Basic ", "").replace("=", "").decodeBase64()
        def userPW = new String(userPWBytes).split(":")
        if (userPW.length < 2) {
            response.addHeader("WWW-Authenticate", "Basic realm=\"metridoc-core\"")
            response.sendError(401)
            return
        }

        if (SecurityUtils.subject.principal) {
            if (!SecurityUtils.subject.principal.equals(userPW[0])) {
                response.addHeader("WWW-Authenticate", "Basic realm=\"metridoc-core\"")
                response.sendError(401)
                return
            }
        }

        try {
            def myUser = ShiroUser.findByUsername(userPW[0])
            if (!myUser) {
                throw new AuthenticationException()
            }
            def myUserPW = myUser.passwordHash
            def testUserPW = new Sha256Hash(userPW[1], userPW[0]).toHex()
            if (myUserPW != testUserPW) {
                throw new AuthenticationException()
            }
            def userRoles = ShiroUser.findByUsername(userPW[0]).getRoles()
            def controllerReport = ManageReport.findByControllerName("${controllerForward}")
            if (controllerReport == null) {
                controllerReport = new ManageReport(controllerName: controllerForward.capitalize())
            }
            def controllerRoles = controllerReport.getRoles()
            log.info controllerRoles
            if (userRoles) {
                if (!controllerRoles || restService.hasCommonRoles(userRoles, controllerRoles)) {
                    def key = UUID.randomUUID().toString()
                    restService.addToRestCache(key, userPW[0])
                    params.put("restKey", key)
                    forward(controller: controllerForward, action: actionForward, params: params)
                } else {
                    response.sendError(403, "You do not have access to see this page")
                    return
                }
            } else if (!controllerRoles) {
                def key = UUID.randomUUID().toString()
                restService.addToRestCache(key, userPW[0])
                params.put("restKey", key)
                forward(controller: controllerForward, action: actionForward, params: params)
            } else {
                response.sendError(403, "You do not have access to see this page")
                return
            }
        } catch (AuthenticationException ex) {
            response.addHeader("WWW-Authenticate", "Basic realm=\"metridoc-core\"")
            response.sendError(401)
            return
        }
    }
}