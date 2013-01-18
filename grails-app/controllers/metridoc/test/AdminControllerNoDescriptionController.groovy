package metridoc.test

class AdminControllerNoDescriptionController {

    static final homePage = [
        adminOnly:true
    ]

    def index() {
        render "I am used for testing only"
    }
}
