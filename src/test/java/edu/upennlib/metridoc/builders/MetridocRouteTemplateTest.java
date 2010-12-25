/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.upennlib.metridoc.builders;

import edu.upennlib.metridoc.routes.MetridocRouteTemplate;
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