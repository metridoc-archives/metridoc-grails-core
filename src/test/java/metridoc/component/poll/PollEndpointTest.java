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

package metridoc.component.poll;

import metridoc.test.StandardTest;
import org.junit.Test;

/**
 *
 * @author tbarker
 */
public class PollEndpointTest extends StandardTest{

    @Test
    public void nothingHappensIfUriIsValid() {
        String validUri = "extract:file:some/path";
        PollEndpoint.validateEndpoint(validUri);
        validUri = "extract://file:some/path";
        PollEndpoint.validateEndpoint(validUri);
    }

    @Test
    public void IllegalArgumentExceptionIsThrownIfUrlIsBad() {
        String invalidUrl = "extract:fooBar?bar=baz"; //requires appended url to send to poll enricher

        try {
            PollEndpoint.validateEndpoint(invalidUrl);
            fail("exception should have occurred");
        } catch (IllegalArgumentException e) {
            
            assertEquals(
                String.format(PollEndpoint.ERROR_MESSAGE, invalidUrl),
                e.getMessage()
            );
        }
    }

    
    

}