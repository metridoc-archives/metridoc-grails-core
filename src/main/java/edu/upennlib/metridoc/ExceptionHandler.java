/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.upennlib.metridoc;

import org.apache.camel.Exchange;

/**
 *
 * @author tbarker
 */
public interface ExceptionHandler {
    void handleBadExchange(Exchange exchange);
    void handleException(String message, Exception exception);
}
