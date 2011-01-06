/**
 * Copyright 2010 Trustees of the University of Pennsylvania Licensed under the
 * Educational Community License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package metridoc.scan;

import metridoc.scan.ClassPathProvider;
import metridoc.scan.DefaultClassScanner;
import org.junit.Before;
import javax.servlet.ServletContext;
import java.io.File;
import java.net.URL;
import org.junit.Assert;
import org.junit.Test;
import static org.easymock.EasyMock.*;

/**
 *
 * @author tbarker
 */
public class DefaultClassScannerTest extends Assert{

    private DefaultClassScanner defaultClassScanner;
    private DefaultClassScanner servletDefaultClassScanner;
    private ClassPathProvider classPathProvider;
    private ServletContext servletContext;
    
    @Before
    public void refresh() {
        defaultClassScanner = null;
        classPathProvider = null;
        servletContext = null;
    }
    
    @Test
    public void canGetAClassFromBasicClassPath() throws Exception {
        replay(classPathProvider());
        assertNotNull(scanner().getClass(Bar.class));
        assertEquals(1, scanner().getClass(Bar.class).size());
        verify(classPathProvider());
    }
    
    @Test
    public void canGetClassFromServletContextBasedClassPath() throws Exception {
        replay(classPathProvider());
        assertNotNull(servletScanner().getClass(Bar.class));
        assertEquals(1, servletScanner().getClass(Bar.class).size());
        verify(classPathProvider());
    }
    
    private DefaultClassScanner servletScanner() throws Exception {
        if (servletDefaultClassScanner == null) {
            servletDefaultClassScanner = new DefaultClassScanner(servletContext());
            servletDefaultClassScanner.setClassPathProvider(classPathProvider());
        }
        
        return servletDefaultClassScanner;
    }
    
    private DefaultClassScanner scanner() throws Exception {
        if (defaultClassScanner == null) {
            defaultClassScanner = new DefaultClassScanner();
            defaultClassScanner.setClassPathProvider(classPathProvider());
        }
        
        return defaultClassScanner;
    }
    
    private ClassPathProvider classPathProvider() throws Exception {
        if (classPathProvider == null) {
            classPathProvider = createMock(ClassPathProvider.class);
            File file = new File("./target/test-classes");
            URL url = file.toURI().toURL();
            URL[] urls = new URL[]{url};
            expect(classPathProvider.getClassPaths()).andReturn(urls).times(0, 1);
            expect(classPathProvider.getClassPaths(servletContext())).andReturn(urls).times(0, 1);
        }
        
        return classPathProvider;
    }
    
    private ServletContext servletContext() {
        if (servletContext == null) {
            servletContext = createMock(ServletContext.class);
        }
        
        return servletContext;
    }

}