package metridoc.core

import org.apache.shiro.SecurityUtils

class WhoamiController {

    static final ANONYMOUS = "anonymous"

    static final homePage = [
            exclude: true
    ]

    def index() {
        def userName = SecurityUtils.subject.principal ? SecurityUtils.subject.principal : ANONYMOUS

        render (contentType: "text/html", text: """
<html>
    <body>${userName}</body>
</html>

""")
    }
}
