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

package edu.upennlib.metridoc.spring.jpa;

import edu.upennlib.metridoc.scan.ClassScanner;
import java.net.URL;
import java.util.Set;
import javax.persistence.Entity;
import javax.servlet.ServletContext;
import org.apache.commons.lang.ArrayUtils;
import org.scannotation.ClasspathUrlFinder;
import org.scannotation.WarUrlFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitPostProcessor;
import org.springframework.web.context.WebApplicationContext;

/**
 *
 * @author Thomas Barker
 */
public class EntityBeanScanner implements PersistenceUnitPostProcessor {

    private ClassScanner classScanner = new ClassScanner();
    @Autowired(required=false)
    private WebApplicationContext applicationContext;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(EntityBeanScanner.class);

    public ClassScanner getClassScanner() {
        return classScanner;
    }

    public void setClassScanner(ClassScanner classScanner) {
        this.classScanner = classScanner;
    }
    
    protected URL[] getUrls() {
        URL[] result = ClasspathUrlFinder.findClassPaths();
        
        if (applicationContext != null) {
            ServletContext servletContext = applicationContext.getServletContext();
            result = (URL[]) ArrayUtils.add(result, WarUrlFinder.findWebInfClassesPath(servletContext));
            result = (URL[]) ArrayUtils.addAll(result, WarUrlFinder.findWebInfLibClasspaths(servletContext));
        }
        
        return result;
    }
        
    
    @Override
    @SuppressWarnings("rawtypes")
    public void postProcessPersistenceUnitInfo(MutablePersistenceUnitInfo pui) {
        
        Set<Class> entities = classScanner.getClasses(Entity.class, getUrls());
        
        for (Class entity : entities) {
            LOGGER.info(String.format("adding entity class %s", entity.getName()));
            pui.addManagedClassName(entity.getName());
        }
    }

    
}
