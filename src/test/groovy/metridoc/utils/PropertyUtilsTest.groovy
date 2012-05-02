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
package metridoc.utils

import metridoc.test.BaseMetridocTest
import org.junit.Test

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 5/27/11
 * Time: 3:34 PM
 */
public class PropertyUtilsTest extends BaseMetridocTest {

    def utils = new PropertyUtils()

    @Test
    def void getPropertyFileThatAlreadyHasExtensionSpecified() {
        assert utils.getConfig("testFiles/metridoc/utils/foo.groovy")
    }

    @Test
    def void testRetrievingInfoBasedOnEnvironments() {
        def utils = new PropertyUtils(environment: "test")
        def config = utils.getConfig("testFiles/metridoc/utils/foo.groovy")
        assert "bar" == config.foobar
        assert config.metridoc.override

        utils = new PropertyUtils(environment: "development")
        config = utils.getConfig("testFiles/metridoc/utils/foo.groovy")
        assert "baz" == config.foobar
        assert !config.metridoc.override
    }

    @Test
    def void getHomeIsNeverNull() {
        assert utils.home
    }

    @Test
    def void getMetridocBaseIsNeverNull() {
        assert utils.metridocHome
    }

    @Test(expected = FileNotFoundException.class)
    def void testNoConfigFileThrowsError() {
        utils.getConfig("notThere")
    }

    @Test
    def void testSystemPathCreation() {
        assert "base${SystemUtils.FILE_SEPARATOR}foo" == utils.systemPath("base", "foo")
    }

    /**
     * after generalizing the logic related to getting properties, we don't really need to test this as
     * thoroughly as the classpath testing
     */
    @Test
    def void testRetrieveFileFromFileSystem() {
        def config = utils.getConfigFileOnSystem("${baseDir}/src/test/resources/testFiles/metridoc/utils/foo")
        assert config.metridoc.test.baz == "from foo.groovy"
        assert null == utils.getConfigFileOnSystem("notThere") //test what happens when it doesn't exist
    }

    @Test
    def void testRetrievingPropertiesFromClassPath() {
        def config = utils.getConfigFileOnClassPath("testFiles/metridoc/utils/foo")
        assert config.metridoc.test.baz == "from foo.groovy"
        config = utils.getConfigFileOnClassPath("testFiles/metridoc/utils/bar", config)
        assert config.metridoc.test.foobar == "from bar.groovy"
        assert config.metridoc.test.baz == "from foo.groovy"
        assert null == utils.getConfigFileOnClassPath("boo")

        config = utils.getConfigFileOnClassPath("boo", config)
        assert config.metridoc.test.foobar == "from bar.groovy"
        config = utils.getConfigFileOnClassPath("testFiles/metridoc/utils/baz")
        assert config.metridoc.test.foobar == "from baz.properties"

    }

    @Test
    def void testConvertPathToFileName() {
        def sep = SystemUtils.FILE_SEPARATOR
        assert utils.convertPathToFileName("blah${sep}foo${sep}bar.txt") == "bar.txt"
        assert utils.convertPathToFileName("boo") == "boo"
    }

}
