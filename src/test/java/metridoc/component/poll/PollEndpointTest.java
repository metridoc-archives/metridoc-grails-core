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