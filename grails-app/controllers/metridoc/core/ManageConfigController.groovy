package metridoc.core

import org.apache.commons.lang.StringUtils
import org.apache.commons.lang.exception.ExceptionUtils

import static metridoc.core.CommonService.METRIDOC_CONFIG

class ManageConfigController {

    def commonService

    static accessControl = {
        role(name: "ROLE_ADMIN")
    }

    def index() {
        [
                metridocConfigExists: commonService.metridocConfig.exists()
        ]
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
