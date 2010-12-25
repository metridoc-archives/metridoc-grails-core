/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.upennlib.metridoc;

import java.util.concurrent.Future;
import org.apache.camel.Service;

/**
 *
 * @author tbarker
 */
public interface FutureHandler extends Runnable {
    void submit(Future future, String batchLabel) throws InterruptedException;
    void finish(String batchLabel);
    void start();
    void stop();
}