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
package metridoc.plugins.camel

import org.junit.Test

import org.apache.camel.spi.Registry
import metridoc.dsl.JobBuilder
import metridoc.dsl.Job;

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 7/4/11
 * Time: 10:36 AM
 */
public class CamelPluginTest {

    def job = JobBuilder.job()

    @Test
    void returnsNullIfNotFound() {
        assert job.getCamelService("foo", String.class) == null
    }

    @Test
    void returnsNullOnTypeMisMatch() {

        job.services["foo"] = true

        //is boolean but trying to get string
        assert job.getCamelService("foo", String.class) == null
    }

    @Test
    void testBasicLookup() {

        job.services["foo"] = "bar"
        assert job.getCamelService("foo", String.class) == "bar"
    }

    @Test
    void canGetUserSpecifiedRegistry() {
        def registry = [] as Registry
        job.services[CamelPlugin.METRIDOC_CAMEL_REGISTRY] = registry
        assert job.registry == registry
    }

    @Test
    void getsDefaultRegistryIfNoneSpecified() {
        assert job.registry
    }

    @Test
    void testRetrievingDefaultCamelContext() {
        assert job.getCamelContext()
    }

}



