package metridoc.admin

import org.codehaus.groovy.grails.commons.GrailsClass

/**
 * Currently working on this.  Intends to display a very simple page on server status and what components are installed
 */
class StatusController {

    static final homePage = [
            exclude: true
    ]

    def index() {
        def model = [:]

        model.workflows = []
        def workflows = model.workflows

        for(workflow in grailsApplication.workflowClasses) {
            workflows.add(workflow as GrailsClass)
        }

        return model
    }
}
