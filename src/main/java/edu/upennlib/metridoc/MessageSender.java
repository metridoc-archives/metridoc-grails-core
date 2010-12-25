/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.upennlib.metridoc;

import java.util.Map;
import java.util.concurrent.Future;

/**
 *
 * @author tbarker
 */
public interface MessageSender {
    void sendBodyAndHeadersToChannel(Object body, Map<String, Object> headers);
    Future asyncSendBodyAndHeadersToChannel(Object body, Map<String, Object> headers);
}
