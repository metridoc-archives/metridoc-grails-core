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

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.ServletContext;
import org.scannotation.AnnotationDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  
 * A basic class scanner which wraps the scannotation library for searching a classpath for classes 
 * with certain annotations.  If this is a web application a servlet context must be set for proper
 * classpath scanning via the appropriate constructor
 * 
 * @author Thomas Barker
 */
public class DefaultClassScanner {
    private ServletContext servletContext;
    private AnnotationDB annotationDb;
    private ClassPathProvider classPathProvider;
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultClassScanner.class);

    /**
     * If one is creating a web application use this constructor
     * 
     * @param servletContext of the web application 
     */
    public DefaultClassScanner(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    /**
     * If one is creating a standalone jar use this constructor
     */
    public DefaultClassScanner() {
    }

    public ClassPathProvider getClassPathProvider() {
        if (classPathProvider == null) {
            classPathProvider = new ClassPathProvider();
        }
        return classPathProvider;
    }

    public void setClassPathProvider(ClassPathProvider classPathProvider) {
        this.classPathProvider = classPathProvider;
    }
    
    public Set<Class> getClass(Class<? extends Annotation> annotation) throws IOException, ClassNotFoundException {
        Set<String> classesText = getAnnotationDb().getAnnotationIndex().get(annotation.getName());
        Set<Class> result = new HashSet<Class>();
        
        for (String className : classesText) {
            
            if (LOGGER.isDebugEnabled()) {
                String annotationName = annotation.getName();
                String message = "scanner found class %s which has annotation %s";
                LOGGER.debug(String.format(message, className, annotationName));
            }
            
            result.add(Class.forName(className));
        }
        
        return result;
    }
    
    protected AnnotationDB instantiateAnnotationDb() {
        return new AnnotationDB();
    }

    /**
     * Essentially a plug for extensibility if the constructor is too limiting.  Every time the default
     * implementation accesses the {@link ServletContext} it is via this getter
     * 
     * @return the servlet context
     */
    protected ServletContext getServletContext() {
        return servletContext;
    }
    
    private AnnotationDB getAnnotationDb() throws IOException {
        if (annotationDb == null) {
            annotationDb = instantiateAnnotationDb();
            configureAnnotationDb(annotationDb);
            
            URL[] urls = getServletContext() == null ? 
                        classPathProvider.getClassPaths() : 
                        classPathProvider.getClassPaths(getServletContext());
            
            annotationDb.scanArchives(urls);
        }
        
        return annotationDb;
    }
    
    /**
     * 
     * Essentially a plug for other implementations to configure the annotationDB.  Currently
     * it is set to only search class level annotations.  see {@link AnnotationDB} for more 
     * information
     * 
     * @param annotationDB the annotationDB to configure
     */
    protected void configureAnnotationDb(AnnotationDB annotationDB) {
        annotationDB.setScanClassAnnotations(true);
        annotationDB.setScanFieldAnnotations(false);
        annotationDB.setScanMethodAnnotations(false);
        annotationDB.setScanParameterAnnotations(false);
    }
    
}
