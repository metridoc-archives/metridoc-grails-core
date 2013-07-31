package metridoc.core

import org.apache.shiro.SecurityUtils

class WhoamiController {

    static final ANONYMOUS = "anonymous"
    static boolean isProtected = true

    def restService

    def index() {
        def userName
        if (params.get("restKey")) userName = restService.getFromRestCache(params.get("restKey"))
        else userName = SecurityUtils.subject.principal ? SecurityUtils.subject.principal : ANONYMOUS

        render(contentType: "text/html", text: """
<html>
    <body>${userName}</body>
</html>

""")
    }
}
