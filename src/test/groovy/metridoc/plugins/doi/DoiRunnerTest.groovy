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

import static org.junit.Assert.*;


import org.slf4j.LoggerFactory;

import org.junit.Ignore;
import org.junit.Test

class DoiRunnerTest {

	private static final log = LoggerFactory.getLogger(DoiRunnerTest.class)
	private static final credentials = loadCredentials();

	@Test
	void testResolveDoi(){
		if(credentials != null){
			def doiRunner = new DoiRunner(userName: credentials.username, password:credentials.password);
			Node n = doiRunner.resolveDoi("10.1577/H02-043");
			org.junit.Assert.assertEquals("Journal of Aquatic Animal Health", n.query_result.body.query.journal_title.text())
			org.junit.Assert.assertEquals("full_text", n.query_result.body.query.publication_type.text())
		}
	}

	@Test
	void testResolveOpenUrl(){
		if(credentials != null){
			def doiRunner = new DoiRunner(userName: credentials.username, password:credentials.password);
			Map params = [title : "Journal of the American College of Cardiology",
						date:2010,
						issue:10,
						aulast:"Jacob",
						volume:55];
			Node n = doiRunner.resolveOpenUrl(params);
			org.junit.Assert.assertEquals("10.1016/j.jacc.2009.08.086", n.query_result.body.query.doi.text())
		}
	}

	@Test
	void testResolveOpenUrlString(){
		if(credentials != null){
			def doiRunner = new DoiRunner(userName: credentials.username, password:credentials.password);
			String paramsStr = "title=Journal+of+the+American+College+of+Cardiology";
			paramsStr += "&date=2010&issue=10&aulast=Jacob&volume=55";
			Node n = doiRunner.resolveOpenUrl(paramsStr);
			org.junit.Assert.assertEquals("10.1016/j.jacc.2009.08.086", n.query_result.body.query.doi.text())
		}
	}
	private static loadCredentials(){
		def credentials = null;
		try{
			String credentialsFilePath = System.getProperty("user.home") + '/.metridoc/crossref/credentials.properties';
			credentials = new ConfigSlurper().parse(new File(credentialsFilePath).toURI().toURL())
		}catch(Exception ex){
			log.warn("Failed to load credentials, tests will be skipped.", ex.getMessage());
		}
		return credentials;
	}
}