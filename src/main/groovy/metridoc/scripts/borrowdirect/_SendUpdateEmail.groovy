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

import groovy.grape.Grape
import groovy.sql.Sql
import metridoc.dsl.JobBuilder

import java.text.SimpleDateFormat
import javax.sql.DataSource

includeTargets << _SendEmailDefaultParameters
JobBuilder.isJob(this)

Grape.grab(group: "org.apache.ant", module: "ant-javamail", version: "1.8.2", classLoader: this.class.classLoader.rootLoader)

target(BDPrintUrlsToSend: "prints the urls that will be sent without actually sending the urls, used for testing") {
    depends(BDValidateAndPrepareParameters)
    getBorrowDirectEmailByUrlMap(borrowDirectSql, new Date()).each {email, url ->
        def message = "sending ${url} to ${email}"
        ant.echo(message: message)
    }
}

target(BDSendUpdateEmail: "actually sends the update email") {

}

target(BDValidateAndPrepareParameters: "validates that the necessary parameters are available") {
    try {
        if (!borrowDirectDataSourceIsDefined()) {
            loadProperties("borrowDirectConfig")
            borrowDirectDataSource = dataSource(borrowDirectConfig.dataSource)
        }
    } catch (Exception ex) {
        ex.printStackTrace()
        ant.echo(message: "Could not load borrowDirectConfig, may not be able to define a dataSource")
    }
    assert borrowDirectDataSourceIsDefined(): "dataSource needs to be defined"
    borrowDirectSql = new Sql(borrowDirectDataSource)
}

borrowDirectDataSourceIsDefined = {
    def result = binding.variables.containsKey("dataSource") || binding.variables.containsKey("borrowDirectDataSource")
    if (result) {
        if (binding.variables.containsKey("borrowDirectDataSource")) {
            return borrowDirectDataSource instanceof DataSource
        } else {
            result = dataSource instanceof DataSource
            if (result) {
                borrowDirectDataSource = dataSource
                return true
            }

            return false
        }
    }

    return false
}


computeBorrowDirectDateRange = {Date date ->
    df = new SimpleDateFormat("yyyyMMdd/")

    beg = date.toCalendar()
    end = date.toCalendar()

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

getBorrowDirectEmailByUrlMap = {Sql sql, date ->
    def emailList = getBorrowDirectReportLinks(sql)
    def dateRange = computeBorrowDirectDateRange(date)
    def result = [:]

    emailList.each {key, value ->
        result[key] = "${borrowDirectBaseUrl}/${dateRange}${value}"
    }

    return result
}