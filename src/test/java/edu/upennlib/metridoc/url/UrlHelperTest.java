/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.upennlib.metridoc.url;

import edu.upennlib.metridoc.url.UrlHelper;
import edu.upennlib.metridoc.url.UrlParam;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author tbarker
 */
public class UrlHelperTest extends Assert {

    @Test
    public void testGetExclusionSet() {
        Set<String> exclusionSet = UrlHelper.getExclusionSet(new FooBar());
        assertEquals(2, exclusionSet.size());
    }
    
    @Test
    public void testBuildUrl() {
        String url = UrlHelper.buildUrl("direct", "foo", new FooBar(null, 0, null));
        assertEquals("direct:foo?age=0", url);
        url = UrlHelper.buildUrl("direct", "foo", new FooBar(null, null, null));
        assertEquals("direct:foo", url);
        url = UrlHelper.buildUrl("direct", "foo", new FooBar("foo", 30, "bar"));
        assertFalse(url.contains("notInUrl=bar"));
    }
    @Test
    public void testGetServiceRef() {
        Set<String> serviceSet = UrlHelper.getServiceReferenceSet(new FooBar());
        assertEquals(1, serviceSet.size());
        assertEquals("service", serviceSet.iterator().next());
    }
    
    @Test
    public void testServiceReference() {
        String url = UrlHelper.buildUrl("direct", "foo", new FooBar(null, 0, "serviceBar", null));
        System.out.println(url);
        assertTrue(url.contains("service=#serviceBar"));
    }
    
    public class FooBar {
        private String name;
        private Integer age;
        @UrlParam(serviceReference=true)
        private String service;
        @UrlParam(include=false)
        private String notInUrl;

        public FooBar() {
        }

        public FooBar(String name, Integer age, String notInUrl) {
            this.name = name;
            this.age = age;
            this.notInUrl = notInUrl;
        }

        public FooBar(String name, Integer age, String service, String notInUrl) {
            this.name = name;
            this.age = age;
            this.service = service;
            this.notInUrl = notInUrl;
        }
        
        public String getService() {
            return service;
        }

        public void setService(String service) {
            this.service = service;
        }

        public String getNotInUrl() {
            return notInUrl;
        }

        public void setNotInUrl(String notInUrl) {
            this.notInUrl = notInUrl;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}