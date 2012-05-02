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
package metridoc.test;


import org.junit.Assert

/**
 *
 * A helpful base test when grabbing files running via maven vs intellij or other IDEs.  Sometimes the IDEs will
 * use a different working directory
 *
 * @author Tommy Barker
 */
class BaseTest extends Assert {

    static File getFile(String path, String alternateBase) {
        def file = new File(path)

        if (!file.exists()) {
            file = new File("${alternateBase}/${path}")
        }

        assert file.exists()
        return file
    }
}

