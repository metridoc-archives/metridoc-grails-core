package metridoc.admin

class LogController {

    static home = [
        [
            action: "index",
            title: "Application Log"
        ]
    ]

    def index() {
        if (params.containsKey('checkAccess')) {
            render 'ACCESS_GRANTED'
            return
        }
    }
}
