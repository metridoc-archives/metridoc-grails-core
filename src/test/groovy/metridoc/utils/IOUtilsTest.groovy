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

package metridoc.utils;


import org.junit.Assert
import org.junit.Ignore
import org.junit.Test

/**
 *
 * @author Tommy Barker
 */
public class IOUtilsTest extends Assert {

    @Test
    public void testGettingFullPathToPropertiesWithJustTheName() {
        String filePath = IOUtils.getPropertyFilePath("fooBar");

        assertEquals(SystemUtils.USER_HOME + SystemUtils.FILE_SEPARATOR + IOUtils.METRIDOC + SystemUtils.FILE_SEPARATOR +
            "fooBar.properties", filePath);

        filePath = IOUtils.getPropertyFilePath("fooBar.properties");

        assertEquals(SystemUtils.USER_HOME + SystemUtils.FILE_SEPARATOR + IOUtils.METRIDOC + SystemUtils.FILE_SEPARATOR +
            "fooBar.properties", filePath);

    }

    //TODO need to get rid of the proxying in here... definitely causing problems
    @Ignore
    @Test
    void testGettingResourcesWithDefaultClassLoader() {
        boolean gotDefaultClassLoader = false
        ClassLoader defaultClassLoader = IOUtils.class.classLoader
        //override getting default classloader
        ClassUtils.metaClass.'static'.getDefaultClassLoader = {
            gotDefaultClassLoader = true
            return defaultClassLoader
        }

        def response = ["bar"]
        def gotResourceWithSpecifiedClassLoader = false
        //override getting resources with specified classloader
        IOUtils.metaClass.'static'.getResources = {String resource, ClassLoader classLoader ->
            assert classLoader == defaultClassLoader
            gotResourceWithSpecifiedClassLoader = true
            return response
        }

        assert IOUtils.getResources("foo") == response
        assert gotDefaultClassLoader
        assert gotResourceWithSpecifiedClassLoader
    }


}