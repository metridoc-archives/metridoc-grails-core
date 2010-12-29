/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.upennlib.metridoc.component;

/**
 *
 * @author tbarker
 */
public @interface Receive {
    public int order() default Integer.MIN_VALUE;
}
