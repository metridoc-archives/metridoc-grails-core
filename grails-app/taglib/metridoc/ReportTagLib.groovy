package metridoc

import org.apache.commons.lang.StringUtils
import org.apache.log4j.Level
import org.springframework.util.Assert

class ReportTagLib {
    static namespace = 'md'
    def logService

    def errorAlert = { attrs, body ->

        if (!attrs) {
            attrs = [:]
        }

        if (!attrs.containsKey("showAlertIf")) {
            attrs.showAlertIf = attrs.alertMessage
        }

        out << render(
                template: "/reports/errorAlert",
                plugin: "metridoc-core",
                model: attrs
        )
    }

    def report = { attrs, body ->
        def layoutInConfig = grailsApplication.config.metridoc.style.layout ? grailsApplication.config.metridoc.style.layout : "main"
        def layout = attrs.layout ? attrs.layout : layoutInConfig
        def model = [layout: layout, body: body.call()]

        model.hasModule = false
        if (attrs.module) {
            model.module = attrs.module
            model.hasModule = true
        }

        //make this work in legacy code
        if ("none" == attrs.module) {
            model.hasModule = false
        }

        out << render(
                template: "/reports/defaultReport",
                plugin: "metridoc-core",
                model: model
        )
    }

    def header = { attrs, body ->
        out << "<strong>${body()}</strong><hr/>"
    }

    def outputLogFile = { attrs, body ->
        def file
        if (attrs.filePath) {
            String path = attrs.filePath
            log.debug "outputting logs from ${path}"
            file = new File(path)
        } else {
            def fileBody = attrs.fileBody
            Assert.notNull(fileBody)
            file = fileBody
        }
        logService.renderLog(out, file)
    }

    def logMsg = { attrs, body ->
        if (attrs['level'] != null) {
            String logLevel =
                Level.toLevel(attrs['level'] as String).toString().toLowerCase()
            log."$logLevel"(body())
        } else {
            log.debug(body())
        }
    }

    def alerts = { attrs ->
        ["alert", "warning", "info", "message"].each {
            def singleMessage = flash."${it}"
            if (singleMessage) {
                flash["${it}s"] << singleMessage
            }
        }
        out << render(
                template: "/reports/alerts",
                plugin: "metridocCore"
        )
    }

    /**
     * Creates a save / cancel bootstrap modal
     *
     * @attr name name of the modal, optional, defaults to controllerName
     * @attr header header of the modal, required
     * @attr action action of the form, optional
     * @attr method method of the form, optional
     * @attr controller controller of the form, optional
     * @attr id id of the form, optional
     * @attr save the value assigned to the save submit button, optional, 'Save changes' by deafault
     * @attr formClass class value for the form, optional
     */
    def saveModal = { attrs, body ->
        Assert.isTrue(attrs.header != null && attrs.header != StringUtils.EMPTY, "header variable MUST be added")
        def model = [
                modalBody: body(),
                modalName: attrs.name ?: controllerName,
                modalHeader: attrs.header,
                modalFormAction: attrs.action,
                modalFormMethod: attrs.method,
                modalFormController: attrs.controller,
                modalFormId: attrs.id,
                modalSaveChanges: attrs.save ?: "Save changes",
                modalFormClass: attrs.formClass
        ]

        out << render(
                template: "/reports/saveModal",
                plugin: "metridocCore",
                model: model
        )
    }

    /**
     * creates a bootstrap button surrounded by the control group tag
     *
     * @attr type type of button, submit by default
     * @attr icon font awesome icon
     *
     * body is the name on the button
     */
    def cgButton = {attrs, body ->
        assert body : "a body for the button must be specified"
        def model = [:]
        model.body = body()
        if(attrs.icon) {
            model.icon = "<i class=\"${attrs.icon}\"></i>"
        }
        model.type = attrs.type ?: "submit"

        out << render(
                template: "/reports/button",
                plugin: "metridocCore",
                model: model
        )
    }
}
