package metridoc

import org.apache.log4j.Level

class ReportTagLib {
    static namespace = 'md'
    def logService

    def report = {attrs, body ->
        def layoutInConfig = grailsApplication.config.metridoc.style.layout?grailsApplication.config.metridoc.style.layout:"main"
        def layout = attrs.layout ? attrs.layout :layoutInConfig
        def model = [layout: layout, body: body]

        if (attrs.module) {
            model.module = attrs.module
        } else {
            model.module = controllerName
        }

        model.hasModule = true
        if("none" == attrs.module) {
            model.hasModule = false
        }

        out << render(
            template: "/reports/defaultReport",
            plugin: "metridoc-core",
            model: model
        )
    }

    def header = {attrs, body ->
        out << "<strong>${body()}</strong><hr/>"
    }

    def outputLogFile = {attrs, body ->
        def path = attrs.filePath
        log.info "outputting logs from ${path}"
        def file = new File(path)
        logService.renderLog(out, file)
    }

    def logMsg = {  attrs, body ->
        if (attrs['level'] != null) {
            String logLevel =
                Level.toLevel(attrs['level']).toString().toLowerCase()
            log."$logLevel"(body())
        }
        else {
            log.debug(body())
        }
    }
}
