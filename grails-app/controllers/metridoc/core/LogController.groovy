package metridoc.core

class LogController {

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

    @SuppressWarnings('EmptyMethod')
    def index() {
    }
}
