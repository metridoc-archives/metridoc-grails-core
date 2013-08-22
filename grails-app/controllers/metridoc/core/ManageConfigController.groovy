package metridoc.core

import org.apache.commons.lang.StringUtils
import org.apache.commons.lang.exception.ExceptionUtils

import static metridoc.core.CommonService.METRIDOC_CONFIG

class ManageConfigController {

    def commonService
    def generalSettingsService
    def dataSource
    def grailsApplication

    static accessControl = {
        role(name: "ROLE_ADMIN")
    }

    @SuppressWarnings("GroovyVariableNotAssigned")
    def index() {
        def startFileExistsAndHasText = generalSettingsService.fileExists(generalSettingsService.startFile)
        def workDirectoryFileExistsAndHasText = generalSettingsService.fileExists(generalSettingsService.workDirectoryFile)
        def command
        def workDirectory
        if (startFileExistsAndHasText) {
            command = startFileExistsAndHasText ? generalSettingsService.startFile.text : null
            workDirectory = workDirectoryFileExistsAndHasText ? generalSettingsService.workDirectoryFile.text : null
        }

        [command: command,
                workDirectory: workDirectory,
                javaCommand: generalSettingsService.javaCommand(),
                javaVmArguments: generalSettingsService.javaVmArguments(),
                mainCommand: generalSettingsService.mainCommand(),
                dataSourceUrl: dataSource.connection.metaData.getURL(),
                applicationName: grailsApplication.config.metridoc.app.name,
                shiroFilters: grailsApplication.config.security.shiro.filter.filterChainDefinitions,
                metridocConfigExists: commonService.metridocConfig.exists()]
    }

    def upload() {
        String fileContent = request.getFile("metridocConfig").inputStream.getText(CommonService.DEFAULT_ENCODING)
        if (fileContent == null || fileContent == StringUtils.EMPTY) {
            flash.alerts << "No file was provided"
            redirect(action: "index")
            return
        }
        def slurper = new ConfigSlurper()
        try {
            slurper.parse(fileContent)
        } catch (Throwable ex) {
            flash.alerts << "Invalid Config! <br /><pre>${ExceptionUtils.getStackTrace(ex)}</pre>"
            redirect(action: "index")
            return
        }
        commonService.metridocConfig.delete()
        commonService.metridocConfig << fileContent
        flash.infos << "File successfully uploaded"
        redirect(action: "index")
    }

    def download() {
        def config = commonService.metridocConfig
        if (config.exists()) {
            response.setContentType("text/groovy")
            response.setHeader("Content-disposition", "attachment;filename=${METRIDOC_CONFIG}")
            response.outputStream << config.bytes
            return
        }

        flash.alerts << "${config} does not exist"
        redirect(action: "index")
    }
}
