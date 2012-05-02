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

import metridoc.plugins.Plugin
import metridoc.utils.IdUtils

/**
 * If a utility is doing everything and the script really isn't getting
 * augmented, then why do we need the plugin?  This class will be removed in a future release
 *
 * @deprecated
 */
@Plugin(category = "job")
class IdPlugin {

    static byte[] md5(Script self, String delimiter, String... itemsToConcatenate) {
        IdUtils.md5(delimiter, itemsToConcatenate)
    }

    static byte[] md5(Script self, String data) {
        IdUtils.md5(data)
    }

    static byte[] uuid() {
        IdUtils.uuid()
    }
}
