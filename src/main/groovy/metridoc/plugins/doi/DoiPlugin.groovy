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
package metridoc.plugins.doi

import metridoc.plugins.Plugin

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 12/21/11
 * Time: 2:47 PM
 * To change this template use File | Settings | File Templates.
 */
@Plugin(category="job")
class DoiPlugin {
	
    static Node resolveDoi(Script script, String doi, String crossRefUserName, String crossRefPassword) {
		def doiRunner = new DoiRunner(userName:crossRefUserName, password: crossRefPassword);
		return doiRunner.resolveDoi(doi);
    }

    static Node resolveOpenUrl(Script script, String urlQuery, String crossRefUserName, String crossRefPassword) {
		def doiRunner = new DoiRunner(userName:crossRefUserName, password: crossRefPassword);
		return doiRunner.resolveOpenUrl(urlQuery);
    }

    static Node resolveOpenUrl(Script script, Map urlQuery, String crossRefUserName, String crossRefPassword) {
		def doiRunner = new DoiRunner(userName:crossRefUserName, password: crossRefPassword);
		return doiRunner.resolveOpenUrl(urlQuery);
    }
}
