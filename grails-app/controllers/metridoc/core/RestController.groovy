package metridoc.core

class RestController {

    static final DEFAULT_ACTION = "index"

    def index() {
        def controllerForward = params.controllerForward
        def actionForward = params.actionForward ?: DEFAULT_ACTION

        forward(controller: controllerForward, action: actionForward, params: params)
    }
}
