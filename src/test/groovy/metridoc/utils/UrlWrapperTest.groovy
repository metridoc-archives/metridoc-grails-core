/*
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
package metridoc.utils

import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 11/11/11
 * Time: 11:42 AM
 */
public class UrlWrapperTest {

    @Test
    void canGetTheHostFromTheUrl() {
        def wrapper = new UrlWrapper(url: "http://foo.com")
        assert "foo.com" == wrapper.host
    }

    @Test
    void canGetThePathAndQueryIfPathAndQuerySet() {
        def wrapper = new UrlWrapper(pathAndQuery: "foo/bar?foo=bar")
        assert "/foo/bar" == wrapper.path
    }

    @Test
    void canGetPathFromUrl() {
        def wrapper = new UrlWrapper(url: "http://foo.com/foo/bar?foo=bar")
        assert "/foo/bar" == wrapper.path
    }

    @Test
    void ifNoPathThenPathIsNull() {
        def wrapper = new UrlWrapper()
        assert null == wrapper.path

        wrapper = new UrlWrapper(url: "http://foo.com?foo=bar")
        assert null == wrapper.path

        wrapper = new UrlWrapper(url: "http://foo.com")
        assert null == wrapper.path
    }

    @Test
    void canGetQueryIfUrlSet() {
        def wrapper = new UrlWrapper(url: "http://foo.com/foo/bar?foo?=bar+baz")
        assert "foo?=bar+baz" == wrapper.query

        wrapper = new UrlWrapper(url: "http://foo.com/foo/bar")
        assert null == wrapper.query
    }

    @Test
    void canGetQueryIfPathAndQuerySet() {
        def wrapper = new UrlWrapper(pathAndQuery: "foo/bar?foo?=bar+baz")
        assert "foo?=bar+baz" == wrapper.query

        wrapper = new UrlWrapper(pathAndQuery: "foo/bar")
        assert null == wrapper.query
    }

    @Test
    void ifPathAndQueryOrUrlIsNotSetPathIsNull() {
        assert null == new UrlWrapper().path
    }

    @Test
    void testGettingPropertyValueValues() {
        def wrapper = new UrlWrapper(url: "http://foo.com/foo/bar?foo=bar+baz")
        assert "bar baz" == wrapper.getPropertyValue("foo")

        wrapper = new UrlWrapper(url: "http://foo.com/foo/bar?foo&b?lah=bar")
        assert null == wrapper.getPropertyValue("foo")
        assert wrapper.containsProperty("foo")
        assert "bar" == wrapper.getPropertyValue("b?lah")
    }
}
