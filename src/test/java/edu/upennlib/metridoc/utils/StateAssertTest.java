/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.upennlib.metridoc.utils;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author tbarker
 */
public class StateAssertTest {

    @Test
    public void testSomeMethod() {
        try {
            StateAssert.notNull(null, "it can't be null");
            fail("exception should have occurred");
        } catch (IllegalStateException e) {
            assertEquals("it can't be null", e.getMessage());
        }
        try {
            StateAssert.notNull(null, "it definitely can't be null");
            fail("exception should have occurred");
        } catch (IllegalStateException e) {
            assertEquals("it definitely can't be null", e.getMessage());
        }

        StateAssert.notNull(new Object(), "some message");
    }

}