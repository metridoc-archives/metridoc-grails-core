package metridoc

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
        def model = [layout: layout, body: body]

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
}
