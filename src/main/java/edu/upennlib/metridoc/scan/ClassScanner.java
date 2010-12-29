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
 * Property of the University of Pennsylvania Libraries
 */

package edu.upennlib.metridoc.scan;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.scannotation.AnnotationDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Thomas Barker
 */
public class ClassScanner {

    private AnnotationDB annotationDB;
    private static final Logger LOGGER = LoggerFactory.getLogger(ClassScanner.class);

    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T extends Annotation> Set<Class> getClasses(Class<T> clazz, URL[] urls) {
        
        LOGGER.info(String.format("scanning for classes annotated with %s", clazz.getName()));
        
        if (annotationDB == null) {
            try {
                annotationDB = new AnnotationDB();
                configureAnnotationDb(annotationDB);
                annotationDB.scanArchives(getUrlsToSearch(urls));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        
        Set<String> classNames = annotationDB.getAnnotationIndex().get(clazz.getName());
        Set<Class> classes = new HashSet<Class>();
        
        if (classNames != null) {
            for (String name : classNames) {
                try {
                    LOGGER.info(String.format("found class %s which is annotated with %s", name, clazz.getName()));
                    classes.add((Class<Object>) Class.forName(name));
                } catch (ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
        
        return classes;
    }
    
    protected static void configureAnnotationDb(AnnotationDB annotationDB) {
        annotationDB.setScanClassAnnotations(true);
        annotationDB.setScanFieldAnnotations(false);
        annotationDB.setScanMethodAnnotations(false);
        annotationDB.setScanParameterAnnotations(false);
    }

    protected URL[] getUrlsToSearch(URL[] urls) {
        return getUrlsToSearch(defaultPackagesToIgnore(), urls);
    }
    
    protected List<String> defaultPackagesToIgnore() {
        ArrayList<String> result = new ArrayList<String>();

        result.add("hibernate");
        result.add("antlr");
        result.add("xml-apis");
        result.add("javax");
        result.add("slf4j");
        result.add("cglib");
        result.add("asm");
        result.add("javaassist");
        result.add("apache");
        result.add("commons");
        result.add("springframework");
        result.add("junit");
        result.add("scannotation");
        result.add("log4j");
        result.add("mysql");
        result.add("hqldb");
        result.add("commons-beanutils");
        result.add("commons-lang");
        result.add("joda-time");
        result.add("joda-time");
        result.add("camel-jpa");

        return result;
    }
    
    protected static URL[] getUrlsToSearch(List<String> defaultUrlsToIgnore, URL[] classpathUrls) {

        List<URL> urlsToSearch = new ArrayList<URL>();

        for (URL url : classpathUrls) {
            if (searchIt(url.toString(), defaultUrlsToIgnore)) {
                LOGGER.info(String.format("will search url %s", url.toString()));
                urlsToSearch.add(url);
            }
        }
        
        return urlsToSearch.toArray(new URL[urlsToSearch.size()]);
    }
    
    protected static boolean searchIt(String url, List<String> defaultUrlsToIgnore) {

        boolean dontIgnore = true;
        int index = 0;
        boolean notDone = index < defaultUrlsToIgnore.size();

        while(dontIgnore && notDone) {
            boolean ignore = url.contains(defaultUrlsToIgnore.get(index));
            index++;
            notDone = index < defaultUrlsToIgnore.size();
            
            if (ignore) {
                LOGGER.info(String.format("will not search url %s", url));
            }
            
            dontIgnore = !ignore;
        }

        return dontIgnore;
    }
}
    