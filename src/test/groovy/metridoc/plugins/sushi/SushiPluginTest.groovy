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

import metridoc.dsl.JobBuilder
import org.apache.http.HttpResponse
import org.junit.Test
import org.apache.http.util.EntityUtils

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 7/17/11
 * Time: 9:12 PM
 */
public class SushiPluginTest {

    @Test
    def void testCallingEuclid() {
        def job = JobBuilder.job()
        Integer status
        String responseText

        use(SushiPlugin) {

            job.callSushi(beginDate: "2008-01-01", endDate: "2008-02-01") {HttpResponse response ->
                status = response.statusLine.statusCode
                responseText = EntityUtils.toString(response.entity)
            }
        }

        assert status == 200
        assert responseText.contains("<ItemPlatform>Project Euclid</ItemPlatform>")
    }
}
