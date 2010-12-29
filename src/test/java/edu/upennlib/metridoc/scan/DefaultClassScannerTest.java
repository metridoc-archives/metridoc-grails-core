/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.upennlib.metridoc.scan;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author tbarker
 */
public class DefaultClassScannerTest extends Assert{

    private DefaultClassScanner defaultClassScanner;
    
    @Test
    public void getUrlsShouldNeverReturnNullOrBeEmpty() throws MalformedURLException {
        assertNotNull(scanner().getUrls());
        assertTrue(scanner().getUrls().length != 0);
    }
    
    @Test
    public void canGetAClassFromBasicClassPath() throws IOException, ClassNotFoundException {
        assertNotNull(scanner().getClass(Bar.class));
        assertEquals(1, scanner().getClass(Bar.class).size());
    }
    
    @Test
    public void canGetClassFromServletContextBasedClassPath() {
        
    }
    
    private DefaultClassScanner scanner() {
        if (defaultClassScanner == null) {
            defaultClassScanner = new TestScanner();
        }
        
        return defaultClassScanner;
    }
    
    class TestScanner extends DefaultClassScanner {

        @Override
        protected URL[] getBasicClassPaths() {
            File file = new File("./target/test-classes");
            URL[] result = null;
            try {
                result = new URL[]{file.toURI().toURL()};
            } catch (MalformedURLException ex) {
                
            }
            
            return result;
        }
    }

}