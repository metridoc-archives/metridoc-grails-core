/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.upennlib.metridoc.utils;

/**
 *
 * @author tbarker
 */
public class StateAssert {
    private StateAssert(){}

    public static void notNull(Object object, String message){
        if (object == null) {
            throw new IllegalStateException(message);
        }
    };
}
