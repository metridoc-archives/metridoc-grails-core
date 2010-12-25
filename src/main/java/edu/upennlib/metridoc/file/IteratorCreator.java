/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.upennlib.metridoc.file;

import java.io.InputStream;
import java.util.Iterator;

/**
 *
 * @author tbarker
 */
public interface IteratorCreator<T> {
    public static final String ITERATOR_CREATOR_NAME = "Metridoc.IteratorCreator.Name";
    Iterator<T> create(InputStream stream);
    boolean supportsExtension(String extension);
    String getName();
}
