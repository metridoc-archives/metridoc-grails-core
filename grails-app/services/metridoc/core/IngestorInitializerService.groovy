package metridoc.core

import org.codehaus.groovy.grails.commons.GrailsClass

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 9/6/12
 * Time: 1:42 PM
 * To change this template use File | Settings | File Templates.
 */
class IngestorInitializerService {

    def grailsApplication

    def init() {
        grailsApplication.ingestorClasses.each {grailsClass ->
            if (!grailsClass.isAbstract()) {
                grailsClass.configure()
            }
        }
    }
}
