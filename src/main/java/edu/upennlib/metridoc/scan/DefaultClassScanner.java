/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.upennlib.metridoc.scan;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.ServletContext;
import org.scannotation.AnnotationDB;
import org.scannotation.ClasspathUrlFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author tbarker
 */
public class DefaultClassScanner {
    private ServletContext servletContext;
    private AnnotationDB annotationDb;
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultClassScanner.class);
    
    public Set<Class> getClass(Class<? extends Annotation> annotation) throws IOException, ClassNotFoundException {
        Set<String> classesText = getAnnotationDb().getAnnotationIndex().get(annotation.getName());
        Set<Class> result = new HashSet<Class>();
        
        for (String className : classesText) {
            result.add(Class.forName(className));
        }
        
        return result;
    }
    
    protected AnnotationDB instantiateAnnotationDb() {
        return new AnnotationDB();
    }
    
    protected URL[] getClassPaths() {
        URL[] result = null;
        
        if (servletContext == null) {
            result = getBasicClassPaths();
        }
        
        return result;
    }
    
    protected URL[] getBasicClassPaths() {
        URL[] result = ClasspathUrlFinder.findClassPaths();
        if (LOGGER.isDebugEnabled()) {
            for (URL url : result) {
                String message = "classpath includes: " + url.toString();
                LOGGER.debug(message);
            }
        }
        
        return ClasspathUrlFinder.findClassPaths();
    }
    
    
    public AnnotationDB getAnnotationDb() throws IOException {
        if (annotationDb == null) {
            annotationDb = instantiateAnnotationDb();
            configureAnnotationDb(annotationDb);
            URL[] urls = getClassPaths();
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
    
    protected URL[] getUrls() throws MalformedURLException {
        return new URL[]{new URL("http://bar")};
    }
}
