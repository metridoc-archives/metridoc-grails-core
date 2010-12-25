/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.upennlib.metridoc.impl.url;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Thomas Barker
 */
public class SqlUrlBuilderTest {

    @Test
    public void testUrl() {
        String url = new SqlUrlBuilder().dataSourceRef("foo").sql("select * from bar where baz = ?").url();
        assertEquals("sql:select * from bar where baz = #?dataSourceRef=foo", url);
    }

}