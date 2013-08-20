package metridoc.core

import org.apache.commons.lang.SystemUtils
import org.apache.commons.lang.exception.ExceptionUtils

class RestartController {

    static accessControl = {
        role(name: "ROLE_ADMIN")
    }

    def index() {
        def startFileExistsAndHasText = fileExists(startFile)
        def workDirectoryFileExistsAndHasText = fileExists(workDirectoryFile)

        if (startFileExistsAndHasText) {
            chain(controller: "manageConfig", action: "index", params: [restartModel: [command: startFileExistsAndHasText ? startFile.text : null,
                    workDirectory: workDirectoryFileExistsAndHasText ? workDirectoryFile.text : null]])
        } else {
            chain(controller: "manageConfig", action: "index")
        }
    }

    def run(String command, String workDirectory) {
        startFile.delete()
        startFile << command

        def workDirectoryUsed = new File(workDirectory)
        def workDirectoryExists = workDirectoryUsed.exists()
        def isADirectory = workDirectoryUsed.isDirectory()
        def invalidDirectory = !workDirectoryExists || !isADirectory
        if (invalidDirectory) {
            flash.alert = "Work Directory $workDirectory does not exist or is not a directory"
            chain(controller: "manageConfig", action: "index")
            return
        }

        workDirectoryFile.delete()
        workDirectoryFile << workDirectory

        [linkToCheck: createLink(controller: "state"),
                homeLink: createLink(controller: "home"),
                triggerRestartLink: createLink(action: "triggerCommand")]
    }

    def triggerCommand() {
        addShutdownHook {
            try {
                new ProcessBuilder("sh", startFile.canonicalPath).directory(new File(workDirectoryFile.text)).start()
            } catch (Exception ex) {
                ex.printStackTrace()
                String fullStackTrace = ExceptionUtils.getFullStackTrace(ex)
                try {
                    File errorFile = getMetridocHomeFile("metridocStartFailure.txt")
                    errorFile << SystemUtils.LINE_SEPARATOR
                    errorFile << SystemUtils.LINE_SEPARATOR
                    errorFile << new Date()
                    errorFile << SystemUtils.LINE_SEPARATOR
                    errorFile << fullStackTrace
                } catch (Exception e) {
                    e.printStackTrace()
                }
            }
        }
        System.exit(0)
    }

    private File getStartFile() {
        getMetridocHomeFile("start.sh")
    }

    private File getWorkDirectoryFile() {
        getMetridocHomeFile("workDir.txt")
    }

    private File getMetridocHomeFile(String fileName) {
        def metridocHome = grailsApplication.mergedConfig.metridoc.home
        return new File("$metridocHome${SystemUtils.FILE_SEPARATOR}${fileName}")
    }

    private static boolean fileExists(File file) {
        file.exists() && file.text.trim()
    }
}
