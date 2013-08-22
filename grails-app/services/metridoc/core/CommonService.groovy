package metridoc.core

import org.apache.commons.lang.SystemUtils

import static org.apache.commons.lang.SystemUtils.*

/**
 * service for commons operations accross services and controllers
 */
class CommonService {

    private static final String DEFAULT_METRIDOC_HOME = "${USER_HOME}${FILE_SEPARATOR}.metridoc"
    public static final String METRIDOC_CONFIG = "MetridocConfig.groovy"
    public static final String DEFAULT_ENCODING = "utf-8"
    def grailsApplication

    def emailIsConfigured() {
        return doEmailIsConfigured(grailsApplication.config)
    }

    private static doEmailIsConfigured(Map configObject) {
        configObject.grails?.mail ? true : false
    }

    File getMetridocConfig() {

        new File("${metridocHome}${FILE_SEPARATOR}${METRIDOC_CONFIG}")
    }

    String getMetridocHome() {
        def metridocHome = grailsApplication.config.metridoc.home
        metridocHome ?: DEFAULT_METRIDOC_HOME
    }
}
