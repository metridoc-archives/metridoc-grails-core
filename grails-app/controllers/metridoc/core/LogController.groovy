package metridoc.core

import grails.web.RequestParameter

class LogController {

    def commonService
    def grailsApplication

    static accessControl = {
        role(name: "ROLE_ADMIN")
    }

    static homePage = [
            title: "Application Log",
            adminOnly: true,
            description: """
                Displays the application log that is normally stored under
                <code>USER_HOME/.metridoc/logs/metridoc.log</code>
            """
    ]

    def index() {
        chain(action: "show")
    }

    @SuppressWarnings('EmptyMethod')
    def show() {
        createModel()
    }

    @SuppressWarnings('EmptyMethod')
    def plain(@RequestParameter("id") String logToDisplay) {
        def model = createModel()
        if (logToDisplay) {
            logToDisplay = logFiles.find { it.contains(logToDisplay) } ?: "${grailsApplication.metadata.getApplicationName()}.log"
            logToDisplay = logToDisplay.contains(".log") ? logToDisplay : logToDisplay + ".log"
            model.put("logToDisplay", logToDisplay)
        }

        return model
    }

    private Map createModel() {

        def files = logFiles
        def initialValue = "${files.find { it.contains(grailsApplication.metadata.getApplicationName()) }}"

        [logFiles: files,
                initialValue: initialValue,
                logToDisplay: initialValue]
    }

    private Set<String> getLogFiles() {
        def logDir = new File("${commonService.metridocHome}/logs")
        getLogFilesFrom(logDir)
    }

    private static Set<String> getLogFilesFrom(File logDir) {
        logDir.listFiles().findAll { File file -> file.isFile() }.collect { File file -> file.name } as Set
    }
}
