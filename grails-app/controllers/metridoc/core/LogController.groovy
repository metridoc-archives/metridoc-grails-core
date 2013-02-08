package metridoc.core

class LogController {

    def logService

    static accessControl = {
        role(name: "ROLE_ADMIN")
    }

    static homePage = [
            title: "Application Log",
            adminOnly: true,
            description: """
                Displays the application log that is normally stored under
                <code>USER_HOME/.metridoc/logs/metridoc.log</code>
            """
    ]

    def index() {
        chain(action: "show")
    }

    @SuppressWarnings('EmptyMethod')
    def show() {

    }

    @SuppressWarnings('EmptyMethod')
    def plain() {

    }
}
