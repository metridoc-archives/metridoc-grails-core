package metridoc.core

/**
 * service for commons operations accross services and controllers
 */
class CommonService {

    def grailsApplication

    def emailIsConfigured() {
        return doEmailIsCnofigured(grailsApplication.config)
    }

    private doEmailIsConfigured(Map configObject) {
        configObject.grails?.mail?.username ? true : false
    }
}
