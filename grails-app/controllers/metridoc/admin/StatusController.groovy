package metridoc.admin

import org.codehaus.groovy.grails.commons.GrailsClass

class StatusController {

    def index() {
        def model = [:]

        model.workflows = []

        grailsApplication.workflowClasses.each {GrailsClass clazz ->
            model.workflow.add(clazz.name)
        }

        return model
    }
}
