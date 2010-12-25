/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.upennlib.metridoc.impl.file;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author tbarker
 */
public class LineIteratorCreatorTest {

    @Test
    public void supportsTextFiles() {
        LineIteratorCreator iteratorCreator = new LineIteratorCreator();
        assertTrue(iteratorCreator.supportsExtension("txt"));
        assertTrue(iteratorCreator.supportsExtension(".txt"));
        assertTrue(iteratorCreator.supportsExtension("csv"));
        assertTrue(iteratorCreator.supportsExtension(".csv"));
        assertTrue(iteratorCreator.supportsExtension("TXT"));
        assertTrue(iteratorCreator.supportsExtension(".TXT"));
        assertTrue(iteratorCreator.supportsExtension("CSV"));
        assertTrue(iteratorCreator.supportsExtension(".CSV"));
        assertFalse(iteratorCreator.supportsExtension(".xls"));
    }

}