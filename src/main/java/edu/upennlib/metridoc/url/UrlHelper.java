/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.upennlib.metridoc.url;

import edu.upennlib.metridoc.url.UrlParam;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author tbarker
 */
public class UrlHelper {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(UrlHelper.class);
    
    public static String buildUrl(String protocol, String name, Object bean) {
        
        String result;
        StringBuilder builder = new StringBuilder(protocol).append(":").append(name);
        try {
            Map<String, String> propertyMap = BeanUtils.describe(bean);
            boolean firstQuery = true;
            
            Set<String> exclusionSet = getExclusionSet(bean);
            Set<String> serviceRef = getServiceReferenceSet(bean);
            for (String key : propertyMap.keySet()) {
                String value = propertyMap.get(key);
                
                if (isParam(key, value, exclusionSet)) {
                    if (firstQuery) {
                        builder.append("?");
                        firstQuery = false;
                    }
                    builder.append(key).append("=");
                    if (serviceRef.contains(key)) {
                        builder.append("#");
                    }
                    builder.append(value).append("&");
                }
            }
            
            
            result = builder.toString();
            if (result.endsWith("&")) {
                result = result.substring(0, result.length() - 1);
            }
            
        } catch (Exception ex) {
            LOGGER.error(String.format("could not analize the properties of %s", bean.getClass().getName()), ex);
            throw new RuntimeException(ex);
        }
        
        return result;
    }
    
    protected static Set<String> getExclusionSet(Object bean) {
        Set<String> exclusionSet = getSetForCondition(bean, new DontIncludeAsParam());
        exclusionSet.add("class");
        return exclusionSet;
    }
    
    protected static Set<String> getServiceReferenceSet(Object bean) {
        return getSetForCondition(bean, new IsServiceReference());
    }
    
    private static Set<String> getSetForCondition(Object bean, Condition condition) {
        Set<String> result = new HashSet<String>();
        try {
            Field[] fields = bean.getClass().getDeclaredFields();
            for (Field field : fields) {
                UrlParam urlParam = field.getAnnotation(UrlParam.class);
                if (condition.condition(urlParam, field.getName())) {
                    result.add(field.getName());
                }
            }
            
        } catch (Exception ex) {
            LOGGER.error(String.format("could not analize the properties of %s", bean.getClass().getName()), ex);
            throw new RuntimeException(ex);
        }
        return result;
    }
    
    private static boolean isParam(String fieldName, String fieldValue, Set<String> exclusionSet) {
        return !StringUtils.isBlank(fieldValue) && 
                !exclusionSet.contains(fieldName);
    }

    interface Condition{
        boolean condition(UrlParam urlParam, String fieldName);
    }
    
    static class IsServiceReference implements Condition {

        @Override
        public boolean condition(UrlParam urlParam, String fieldName) {
            return urlParam != null && urlParam.serviceReference();
        }
        
    }
    
    static class DontIncludeAsParam implements Condition {

        @Override
        public boolean condition(UrlParam urlParam, String fieldName) {
            return urlParam != null && !urlParam.include();
        }
        
    }

}
