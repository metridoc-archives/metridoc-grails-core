package metridoc.admin

import org.codehaus.groovy.grails.commons.GrailsClass

class StatusController {

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
