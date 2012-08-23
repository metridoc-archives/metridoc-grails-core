package metridoc.admin

class LogController {

    def index(){
        println "Here"
        def logPath=grailsApplication.config.metridoc.home+"/logs/metridoc.log"
        def logFile = new File(logPath)
        def logResponse = "$logFile's contents: ${logFile.getText()}";
        render(view:  "/log/index.gsp", contentType: "text/html", model:[logResponse:logResponse])
        //"$logFile's contents: ${logFile.getText()}"
    }
}
