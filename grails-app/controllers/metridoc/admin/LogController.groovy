package metridoc.admin

class LogController {

    def logService

    def index(){
        def logPath=grailsApplication.config.metridoc.home+"/logs/metridoc.log"
        def logFile = new File(logPath)
        def logResponse = "$logFile's contents:\n ${logFile.getText()}";

        logResponse = logService.escape(logResponse)

        logResponse = logService.addDiv(logResponse)

        render(view:  "/log/index.gsp", contentType: "text/html", model:[logResponse:logResponse])
        //"$logFile's contents: ${logFile.getText()}"
    }


}
