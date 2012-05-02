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
package metridoc.camel.registry

import metridoc.test.BaseTest
import org.apache.camel.impl.SimpleRegistry
import org.junit.Test

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 4/16/11
 * Time: 11:36 PM
 *
 * wraps one or more {@link org.apache.camel.spi.Registry} and retrieves services based on the order the registries
 * are added to the wrapper.  So the last registry added take precedence over previous ones
 */
class RegistryWrapperTest extends BaseTest{

    @Test
    def void testLookup() {
        def foo = new SimpleRegistry()
        foo["foo"] = "bar"
        def bar = new SimpleRegistry()
        bar["foo"] = "foobar"
        def wrapper = new RegistryWrapper()
        wrapper.registries.add foo
        wrapper.registries.add bar
        assert "foobar" == wrapper.lookup("foo")

        bar["bar"] = "baz"
        assert "baz" == wrapper.lookup("bar")
    }

    @Test
    def void testLookupWhenFirstRegistryHasServiceButLastDoesNot() {
        def foo = new SimpleRegistry()
        foo["foo"] = "bar"
        def bar = new SimpleRegistry()
        bar["bar"] = "baz"
        def wrapper = new RegistryWrapper()
        wrapper.registries.add foo
        wrapper.registries.add bar

        assert "bar" == wrapper.lookup("foo")
    }

    @Test
    def void testLookupByType() {
        def foo = new SimpleRegistry()
        foo["foo"] = "bar"
        def bar = new SimpleRegistry()
        bar["bar"] = "baz"
        def wrapper = new RegistryWrapper()
        wrapper.registries.add foo
        wrapper.registries.add bar

        assert wrapper.lookupByType(String.class).values().contains("bar")
        assert wrapper.lookupByType(String.class).values().contains("baz")
    }
}
