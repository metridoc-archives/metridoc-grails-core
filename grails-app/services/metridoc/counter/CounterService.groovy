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
package metridoc.counter

class CounterService {

    def grailsApplication

    def static final filePattern = {int year, String reportType ->
        /^.*\/counter${reportType}_${year}(_\d+)?\.xlsx?$/
    }

    def getCounterDirectoryPath() {
        grailsApplication.config.metridoc.counter.directory
    }

    def getCounterDirectory() {
        new File(counterDirectoryPath)
    }

    /**
     * Populate counter's selection box with available year directory
     * @return list of years who has a corresponding directory under counterDirectory
     */
    def List getYearsAvailable() {
        def yearList = []
        def directoryExistsAndIsADirectory = counterDirectory.exists() && counterDirectory.isDirectory()
        if (directoryExistsAndIsADirectory) {
            counterDirectory.eachDir {
                def fileList = []
                def directoryByYear = new File("${counterDirectoryPath}/${it.name}")
                if (directoryByYear.exists() && directoryByYear.isDirectory()) {
                    directoryByYear.eachFile {
                        if (!it.name.startsWith('.')) {
                            fileList << it.name
                        }
                    }
                }
                if (fileList.size() && it.name.isInteger()) {
                    yearList << it.name
                }
            }
        }
        return yearList.sort()
    }

    /**
     * Find all fileTypes exist in the directory of the given year
     * @param year
     * @return a map whose keys represents file Types and values are the contents to display
     */
    def Map getDirectoryContentInJSON(String year) {
        def fileList = [:]
        def directoryExistsAndIsADirectory = counterDirectory.exists() && counterDirectory.isDirectory()
        if (directoryExistsAndIsADirectory) {
            counterDirectory.eachFile {
                if (it.name == year) {
                    log.info("in getDirectoryContentInJSON year: ${it.name}")
                    def directoryByYear = new File("${getCounterDirectoryPath()}/${it.name}")
                    if (directoryByYear.exists() && directoryByYear.isDirectory()) {
                        directoryByYear.eachFile {
                            if (!it.name.startsWith('.')) {
                                if (it.name =~ 'JR1') {
                                    fileList.put('JR1', 'Journal Report 1: Number of Successful Full-Text Article Requests by\n' +
                                            '        Month and Journal')
                                } else if (it.name =~ 'JR2') {
                                    fileList.put('JR2', 'Journal Report 2: Turnaways by Month and Journal')
                                } else if (it.name =~ 'BR1') {
                                    fileList.put('BR1', 'Book Report 1: Number of Successful Title Requests by Month and Title')
                                } else if (it.name =~ 'BR2') {
                                    fileList.put('BR2', 'Book Report 2: Number of Successful Section Requests by Month and Title')
                                } else if (it.name =~ 'BR4') {
                                    fileList.put('BR4', 'Book Report 4: Turnaways by Month and Service')
                                } else if (it.name =~ 'BR5') {
                                    fileList.put('BR5', 'Book Report 5: Total Searches and Sessions by Month and Title')
                                } else if (it.name =~ 'BR6') {
                                    fileList.put('BR6', 'Book Report 6: Total Searches and Sessions by Month and Service')
                                } else if (it.name =~ 'DB1') {
                                    fileList.put('DB1', 'Database Report 1: Total Searches and Sessions by Month and Database')
                                } else if (it.name =~ 'DB2') {
                                    fileList.put('DB2', 'Database Report 2: Turnaways by Month and Database')
                                } else if (it.name =~ 'DB3') {
                                    fileList.put('DB3', 'Database Report 3: Total Searches and Sessions by Month and Service')
                                }
                            }
                        }
                    }
                }
            }
        }
        log.info("fileList:" + fileList)
        return fileList
    }

    /**
     * Returns the report file of given report type and under the given year
     * @param reportType
     * @param reportYear
     * @return the most recent file of the given year and fileType
     */
    def File getReportFile(String reportType, int reportYear) {

        log.error "searching for report type ${reportType} and report year ${reportYear}"
        def directoryExistsAndIsADirectory = counterDirectory.exists() && counterDirectory.isDirectory()

        if (directoryExistsAndIsADirectory) {
            def fileList = []
            def directoryByYear = new File("${counterDirectoryPath}/${reportYear}")
            directoryByYear.eachFile {
                if (it.isFile()) {
                    fileList << it.canonicalPath
                }
            }
            def fileName = getFileName(reportYear, reportType, fileList)
            if (fileName) {
                log.info "will return file ${fileName} from search with report type ${reportType} and year ${reportYear}"
                return new File(fileName)
            }
        }
        return null
    }

    /**
     * Returns the most recent journal of the given year and type in the given list
     * @param year of the report
     * @param journalName journal name / type
     * @param files files to retrieve journal from
     * @return name of the most recent file in the given file list
     */
    private static String getFileName(int year, String journalName, List<String> filePaths) {
        def result = null
        filePaths.sort().each {
            if (it ==~ filePattern.call(year, journalName)) {
                result = it
            }
        }
        return result
    }

}
