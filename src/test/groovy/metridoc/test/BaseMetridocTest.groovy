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
package metridoc.test

import metridoc.utils.SystemUtils

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 5/30/11
 * Time: 3:54 PM
 */
abstract class BaseMetridocTest {
    public static final METRIDOC_CORE = "metridoc-core"
    public static final CORE_DIR = "core"
    public static final METRIDOC_CORE_DIR = "${CORE_DIR}${SystemUtils.FILE_SEPARATOR}${METRIDOC_CORE}"

    def String getBaseDir() {
        if (new File(METRIDOC_CORE_DIR).exists()) {
            return METRIDOC_CORE_DIR
        }

        return SystemUtils.USER_DIR
    }

}
