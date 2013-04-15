package metridoc.test

class SimpleController {

    static homePage = [
            title: "Simple Controller",
            description: """
                A simple controller that doesn't do anything.  Used for testing, such as setting authentication stuff.. etc.
            """
    ]

    def index() {
        render "I am used for testing only"
    }
}
