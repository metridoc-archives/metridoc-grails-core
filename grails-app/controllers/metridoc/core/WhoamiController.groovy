package metridoc.core

import org.apache.shiro.SecurityUtils

class WhoamiController {

    def index() {

        render (contentType: "text/html", text: """
<html>
    <body>${SecurityUtils.subject.principal}</body>
</html>

""")
    }
}
