/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.upennlib.metridoc.impl.url;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author tbarker
 */
public class SedaUrlBuilderTest extends Assert {

    @Test
    public void testUrl() {
        String url = new SedaUrlBuilder().size(5)
                .waitForTaskToComplete(SedaUrlBuilder.WaitForTaskToComplete.IfReplyExpected).url();
        assertTrue(url.contains("waitForTaskToComplete=IfReplyExpected"));
    }

}