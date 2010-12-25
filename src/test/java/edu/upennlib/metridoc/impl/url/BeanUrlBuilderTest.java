/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.upennlib.metridoc.impl.url;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Thomas Barker
 */
public class BeanUrlBuilderTest {


    @Test
    public void testSomeMethod() {
        assertEquals("bean:fooService", new BeanUrlBuilder().name("fooService").url());
        assertEquals("bean:fooService?method=bar", new BeanUrlBuilder().name("fooService").method("bar").url());
        
        try {
            new BeanUrlBuilder().url();
            fail("exception should have occurred");
        } catch (IllegalArgumentException e) {
        }
    }

}