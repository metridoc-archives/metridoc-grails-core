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

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 12/21/11
 * Time: 3:07 PM
 *
 *  actually does the work of calling the crossRef service
 *
 */
class DoiRunner {
	static String DOI_URL = "http://www.crossref.org/openurl/?noredirect=true&pid=";

	String userName
	String password

	Node resolveDoi(String doi) {
		return processRequest("&id=" + doi);
	}

	Node resolveOpenUrl(String urlQuery) {
		processRequest("&"+urlQuery);
	}

	Node resolveOpenUrl(Map params) {
		def urlQuery = params.collect{ key, value -> key+"="+URLEncoder.encode(value+"", "UTF-8") }.join("&");
		return resolveOpenUrl(urlQuery);
	}

	private Node processRequest(paramsStr){
		def resultStr = new URL( getCrossrefUrlPrefix() + paramsStr).text;
		Node node = new XmlParser().parseText(resultStr);
		return node;
	}

	private String getCrossrefUrlPrefix(){
		return DOI_URL + userName + ":"+password
	}
}
