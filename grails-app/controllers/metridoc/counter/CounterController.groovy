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

import metridoc.ReportController

class CounterController extends ReportController {

    def static reportName = "Counter Reports"
    private static final String CONTENT_TYPE_EXCEL_2007 = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private static final String CONTENT_TYPE_EXCEL_2003 = "application/vnd.ms-excel";
    private static final String FILE_EXTENSION_EXCEL_2007 = "xlsx";

    def counterService
    def grailsApplication

    @Override
    def getModel() {
        def years = counterService.getYearsAvailable()

        return [
            yearList: years,
            fileTypeList: counterService.getDirectoryContentInJSON(years[0]),
            path: counterService.counterDirectoryPath
        ]
    }

    /**
     * Get a map whose keys are file types contained by the year being selected
     * @return a map represented in JSON whose keys are file-type shortcuts and values are corresponding file-type descriptions
     */
    def getDirectoryContentInJSON() {
        def year = params.get('selected')
        def fileList = counterService.getDirectoryContentInJSON(year)
        render(contentType: 'text/json') {
            fileList
        }
    }
    /**
     * Download the most recent file of the chosen type among the given file-type list
     */
    def downloadFile() {
        String reportType = params.counter_group;
        int year = params.int("year");
        File file = counterService.getReportFile(reportType, year);

        if (file != null && file.exists()) {
            log.info("downloading file ${file}")
            if (file.getName().endsWith(FILE_EXTENSION_EXCEL_2007)) {
                response.setContentType(CONTENT_TYPE_EXCEL_2007);
            } else {
                response.setContentType(CONTENT_TYPE_EXCEL_2003);
            }
            response.addHeader("Content-Disposition", "attachment; filename=${file.name}");
            response.setContentLength((int) file.length());
            response.outputStream << file.bytes
        }
    }
}

