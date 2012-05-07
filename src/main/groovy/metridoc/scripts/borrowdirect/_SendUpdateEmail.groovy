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
package metridoc.scripts.borrowdirect

import java.text.SimpleDateFormat
import javax.sql.DataSource

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 5/7/12
 * Time: 1:34 PM
 */
target(printUrlsToSend: "prints the urls that will be sent without actually sending the urls, used for testing") {

}

target(sendUpdateEmail: "actually sends the update email") {

}

target(validateParameters: "validates that the necessary parameters are available") {
    def borrowDirectDataSourceIsDefined

}

borrowDirectDataSourceIsDefined = {
    def result = binding.variables.containsKey("dataSource") || binding.variables.containsKey("borrowDirectDataSource")
    if(result) {
        if(binding.variables.containsKey("dataSource")) {
            return dataSource instanceof DataSource
        } else {
            return borrowDirectDataSource instanceof DataSource
        }
    }
}

borrowDirectDateRange = {
    df = new SimpleDateFormat("yyyyMMdd/")

    beg = Calendar.instance
    end = Calendar.instance

    Y = beg.get(Calendar.YEAR)
    M = beg.get(Calendar.MONTH)
    D = beg.get(Calendar.DATE)

    if (M == 0) {
        beg.roll(Calendar.YEAR, -1)
        end.roll(Calendar.YEAR, -1)
    }
    beg.roll(Calendar.MONTH, -1)
    beg.roll(Calendar.DATE, (-D + 1))
    end.roll(Calendar.MONTH, -1)
    end.roll(Calendar.DATE, -D)

    def startDate = df.format(beg.time)
    def endDate = df.format(end.time)

    return '' + startDate + endDate
}
