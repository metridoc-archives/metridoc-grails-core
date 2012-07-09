/*
 * Copyright 2010 Trustees of the University of Pennsylvania Licensed under the
 * Educational Community License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package metridoc.data

class DataController {

    def grailsApplication

    def index() {
        [dataTypes: ["file"]]
    }

    def file() {

        if (params.id) {
            params.projectName = params.id
        }

        def result = []

        def containsProjectAndFileData =
            grailsApplication.config.metridoc.containsKey(params.projectName) &&
                grailsApplication.config.metridoc[params.projectName].containsKey("data") &&
                grailsApplication.config.metridoc[params.projectName].data.containsKey("file")


        if (containsProjectAndFileData) {
            def directory = grailsApplication.config.metridoc[params.projectName].data.file.directory
            def path = directory + "/" + (params.filePath ? params.filePath : "")
            def file = new File(path)

            log.info "searching directory / file ${file}"
            if (file.isDirectory()) {
                def fileList = []

                file.listFiles().each {
                    def fileName = it.name
                    if (!fileName.startsWith(".")) {
                        if (it.isDirectory()) {
                            fileList.add it.name + "/"
                        } else {
                            fileList.add it.name
                        }
                    }
                }

                result = [fileList: fileList]
            } else {
                response.addHeader("Content-Disposition", "attachment; filename=${file.name}")
                response.setContentLength((int) file.length())
                def extension = file.name.substring(file.name.lastIndexOf(".") + 1)
                response.setContentType(grailsApplication.config.grails.mime.types[extension])
                response.outputStream << file.bytes
            }

        }
        if (result) result
    }

}
