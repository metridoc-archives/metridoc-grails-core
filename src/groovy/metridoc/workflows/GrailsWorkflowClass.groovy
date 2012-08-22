package metridoc.workflows

import org.codehaus.groovy.grails.commons.InjectableGrailsClass

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 8/22/12
 * Time: 1:50 PM
 * To change this template use File | Settings | File Templates.
 */
interface GrailsWorkflowClass extends InjectableGrailsClass {
    def run()
}
