/**
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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package metridoc.builders;

import metridoc.routes.MetridocRouteTemplate;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author tbarker
 */
public class MetridocRouteTemplateTest {

    @Test
    public void shouldFailWhenStartOrEndNotSpecified() throws Exception {

        try {
            mockedTemplate.configure();
            fail("exception should have occurred");
        } catch (IllegalStateException e) {
            assertEquals("startFrom needs to be specified", e.getMessage());
        }
    }

    MetridocRouteTemplate mockedTemplate = new MetridocRouteTemplate<MetridocRouteTemplate>() {

        @Override
        public void route() throws Exception {
        }
    };

}