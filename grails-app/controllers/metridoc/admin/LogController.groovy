package metridoc.admin

class LogController {

    static home = [
        [
            action: "index",
            title: "Application Log",
            category: "Administration",
            description: """
                Displays the application log that is normally stored under <code>USER_HOME/.metridoc/logs/metridoc.log</code>
            """
        ]
    ]

    def index() {
        if (params.containsKey('checkAccess')) {
            render 'ACCESS_GRANTED'
            return
        }
    }
}
