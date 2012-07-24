package metridoc

class ReportTagLib {
    static namespace = 'md'

    def report = {attrs, body ->
        def layout = attrs.layout ? attrs.layout : "main"
        out << render(
            template:  "/reports/defaultReport",
            plugin: "metridoc-core",
            model: [layout: layout, body: body],
        )
    }

    def header = {attrs, body ->
        out << "<strong>${body()}</strong><hr/>"
    }
}
