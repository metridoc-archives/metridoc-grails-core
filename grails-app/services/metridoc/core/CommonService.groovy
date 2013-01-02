package metridoc.core

/**
 * service for commons operations accross services and controllers
 */
class CommonService {

    def grailsApplication

    def emailIsConfigured() {
        return doEmailIsConfigured(grailsApplication.config)
    }

    private static doEmailIsConfigured(Map configObject) {
        configObject.grails?.mail ? true : false
    }
}
