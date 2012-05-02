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
package metridoc.plugins.sushi

import groovy.text.SimpleTemplateEngine
import java.text.SimpleDateFormat
import org.apache.http.HttpResponse
import org.apache.http.auth.AuthScope
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.ResponseHandler
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.DefaultHttpClient
import metridoc.plugins.Plugin

@Plugin(category = "job")
class SushiPlugin {

    def static callSushi(Script self, LinkedHashMap args, Closure closure) {
        def template = new SushiTemplate(args)
        def client = new DefaultHttpClient()
        def post = new HttpPost(template.sushiUrl)
        post.entity = new StringEntity(template.soap, "text/xml", "utf-8")
        post.setHeader("SOAPAction", template.soapAction)

        if (template.basicUsername && template.basicPassword) {
            client.getCredentialsProvider().setCredentials(
                    AuthScope.ANY,
                    new UsernamePasswordCredentials(template.basicUsername, template.basicPassword));
        }

        def responseHandler = new ClosureResponseHandler(closure: closure)
        client.execute(post, responseHandler)
    }
}

class ClosureResponseHandler implements ResponseHandler {

    Closure closure

    Object handleResponse(HttpResponse response) {
        closure.call(response)
    }
}

class SushiTemplate {

    String defaultText = "NA"
    def reportRequestId = defaultText
    def dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    def created = dateFormatter.format(new Date())
    def requestorId = "demo"
    def requestorName = defaultText
    def requestorEmail = defaultText
    def customerId = "demo"
    def customerName = defaultText
    def reportName = "JR1"
    def reportRelease = 3
    def beginDate = "2008-01-01";
    def endDate = "2009-12-31";
    def sushiUrl = "http://projecteuclid.org/sushi/16/service.cgi"
    def basicUsername
    def basicPassword
    def sushiProviderName
    def soapAction = "SushiService:GetReportIn"
    def soapHeader = ""

    def soapTemplate = '''
            <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:coun="http://www.niso.org/schemas/sushi/counter"
                  xmlns:sus="http://www.niso.org/schemas/sushi">

                <soapenv:Header>${soapHeader}</soapenv:Header>
                <soapenv:Body>
                    <coun:ReportRequest Created="${created}" ID="${reportRequestId}">
                        <sus:Requestor>
                            <sus:ID>${requestorId}</sus:ID>
                            <sus:Name>${requestorName}</sus:Name>
                            <sus:Email>${requestorEmail}</sus:Email>
                        </sus:Requestor>
                        <sus:CustomerReference>
                            <sus:ID>${customerId}</sus:ID>
                            <!--Optional:-->
                            <sus:Name>${customerName}</sus:Name>
                        </sus:CustomerReference>
                        <sus:ReportDefinition Name="${reportName}" Release="${reportRelease}">
                            <sus:Filters>
                                <sus:UsageDateRange>
                                    <sus:Begin>${beginDate}</sus:Begin>
                                    <sus:End>${endDate}</sus:End>
                                </sus:UsageDateRange>
                            </sus:Filters>
                        </sus:ReportDefinition>
                    </coun:ReportRequest>
                </soapenv:Body>
            </soapenv:Envelope>
        '''

    String getSoap() {
        def binding = [reportRequestId: reportRequestId,
                created: created,
                requestorId: requestorId,
                requestorName: requestorName,
                requestorEmail: requestorEmail,
                customerId: customerId,
                soapHeader: soapHeader,
                customerName: customerName,
                reportName: reportName,
                reportRelease: reportRelease,
                beginDate: beginDate,
                endDate: endDate,
        ]


        def engine = new SimpleTemplateEngine()
        engine.createTemplate(soapTemplate).make(binding).toString()
    }
}
