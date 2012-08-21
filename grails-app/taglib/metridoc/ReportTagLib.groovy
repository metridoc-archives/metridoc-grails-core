package metridoc

import org.apache.log4j.Level

class ReportTagLib {
    static namespace = 'md'

    def getStyleLayoutInConfig = {
        String style = "main"
        File configFile = new File("${System.getProperty('user.dir')}/grails-app/conf/Config.groovy")
        configFile.eachLine {aLine->
            if(aLine.contains('metridoc.style.layout =')){
                style = aLine.split('metridoc.style.layout =')[1]
                log.info "found style layout ${style} in config"
            }
        }
        return style
    }

    def report = {attrs, body ->
        def layout = attrs.layout ? attrs.layout : getStyleLayoutInConfig()
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
