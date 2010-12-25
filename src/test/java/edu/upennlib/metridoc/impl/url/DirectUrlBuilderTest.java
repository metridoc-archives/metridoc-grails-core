/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.upennlib.metridoc.impl.url;

import edu.upennlib.metridoc.impl.url.DirectUrlBuilder;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Thomas Barker
 */
public class DirectUrlBuilderTest {


    @Test
    public void testUrl() {
        DirectUrlBuilder direct = new DirectUrlBuilder().name("test").id("foo");
        assertEquals("direct:test-foo", direct.url());
    }

}