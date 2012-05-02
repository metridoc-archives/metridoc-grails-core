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
package metridoc.plugins.sql

import org.junit.Test
import metridoc.plugins.PluginDB
import metridoc.dsl.JobBuilder;

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 11/14/11
 * Time: 3:50 PM
 */
public class UrlPluginTest extends Script {

    @Test
    void testThatUrlPluginIsRegistered() {
        def registered = false
        PluginDB.instance.getPlugins("job").each {
            if (it == UrlPlugin) registered = true
        }
        assert registered
    }

    @Test
    void testBasicFunctionality() {
        this.run()
    }

    @Override
    Object run() {
        use(UrlPlugin) {
            def wrapper = wrapUrl("http://foo.com")
            assert "foo.com" == wrapper.host
        }

        JobBuilder.isJob(this)
        def wrapper = wrapUrl("http://foo.com")
        assert "foo.com" == wrapper.host

        wrapper = wrapUrl("http://foo.com?bar=baz&fooBar&blam=boom+bam")
        assert wrapper.queryMap.containsKey("bar")
        assert wrapper.queryMap.containsKey("fooBar")
        assert wrapper.queryMap.containsKey("blam")

        assert "boom bam" == wrapper.getPropertyValue("blam")
        assert "boom bam" == wrapper.queryMap.blam
        assert "boom bam" == wrapper.queryMap["blam"]
    }
}

