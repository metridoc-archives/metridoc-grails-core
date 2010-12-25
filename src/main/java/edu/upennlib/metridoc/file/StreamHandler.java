/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.upennlib.metridoc.file;

import java.io.InputStream;
import java.util.Map;

/**
 *
 * @author tbarker
 */
public interface StreamHandler {
    void handleStream(InputStream inputStream, Map<String, Object> messageHeaders);
}
