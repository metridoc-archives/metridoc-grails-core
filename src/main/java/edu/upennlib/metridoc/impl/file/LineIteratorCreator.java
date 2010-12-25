/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.upennlib.metridoc.impl.file;

import edu.upennlib.metridoc.file.IteratorCreator;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import org.apache.commons.io.LineIterator;

/**
 *
 * @author tbarker
 */
public class LineIteratorCreator implements IteratorCreator<String>{

    public static final String BEAN_NAME = "lineIteratorCreator";

    @Override
    public Iterator<String> create(InputStream stream) {
        return new LineIterator(new InputStreamReader(stream));
    }

    @Override
    public boolean supportsExtension(String extension) {
        return
                "txt".equalsIgnoreCase(extension) || ".txt".equalsIgnoreCase(extension) ||
                "csv".equalsIgnoreCase(extension) || ".csv".equalsIgnoreCase(extension);
    }

    @Override
    public String getName() {
        return DefaultIteratorNames.TXT.getDescription();
    }

}
