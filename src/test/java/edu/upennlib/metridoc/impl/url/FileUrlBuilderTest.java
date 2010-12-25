/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.upennlib.metridoc.impl.url;

import edu.upennlib.metridoc.impl.url.FileUrlBuilder;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author tbarker
 */
public class FileUrlBuilderTest {


    @Test
    public void testUrl() {
        String url = new FileUrlBuilder().fromFolder("foo").filter("bar").url();
        assertTrue(url.startsWith("file:foo"));
        assertFalse(url.contains("fromFolder=foo"));
        assertTrue(url.contains("filter=#bar"));
    }

}