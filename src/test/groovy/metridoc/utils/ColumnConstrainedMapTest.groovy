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
 * Date: 10/7/11
 * Time: 2:05 PM
 */
public class ColumnConstrainedMapTest {

    @Test(expected=IllegalArgumentException.class)
    void throwsExceptionIfEmptyMapIsPassed() {
        new ColumnConstrainedMap([:], ["foo"] as Set)
    }

    @Test(expected=IllegalArgumentException.class)
    void throwsExceptionIfEmptyColumnsIsPassed() {
        new ColumnConstrainedMap([foo:"bar"], [] as Set)
    }

    @Test(expected=IllegalArgumentException.class)
    void throwsExceptionIfNullMapIsPassed() {
        new ColumnConstrainedMap(null, ["foo"] as Set)
    }

    @Test(expected=IllegalArgumentException.class)
    void throwsExceptionIfNullColumnsIsPassed() {
        new ColumnConstrainedMap([foo:"bar"], null)
    }

    @Test
    void containsKeyIsOnlyTrueIfKeyIsInWrappedMapAndColumnsSet() {
        def map = new ColumnConstrainedMap([foo:"bar", baz:"bar"], ["bar", "baz"] as Set)
        assert map.containsKey("baz")
        assert !map.containsKey("foo")
    }

    @Test
    void containsValueIsOnlyTrueIfCorrespondingKeyFromWrappedMapIsInColumnsSet() {
        def map = new ColumnConstrainedMap([foo:"foobar", baz:"bar", foobar: null], ["bar", "baz", "foobar"] as Set)
        assert map.containsValue("bar")
        assert !map.containsValue("foobar")
        assert map.containsValue(null)
    }

    @Test
    void canOnlyRetrieveObjectsWhosKeysAreInColumnSetAndWrappedMapOtherwiseNull() {
        def map = new ColumnConstrainedMap([foo:"foobar", baz:"bar", foobar: null], ["bar", "baz", "foobar"] as Set)
        assert map.baz == "bar"
        assert map.foo == null
    }

    @Test(expected=UnsupportedOperationException.class)
    void putNotSupported() {
        new ColumnConstrainedMap([foo:"foobar", baz:"bar", foobar: null], ["bar", "baz", "foobar"] as Set).put("foo", "bar")
    }

    @Test(expected=UnsupportedOperationException.class)
    void removeNotSupported() {
        new ColumnConstrainedMap([foo:"foobar", baz:"bar", foobar: null], ["bar", "baz", "foobar"] as Set).remove("foobar")
    }

    @Test(expected=UnsupportedOperationException.class)
    void putAllNotSupported() {
        new ColumnConstrainedMap([foo:"foobar", baz:"bar", foobar: null], ["bar", "baz", "foobar"] as Set).putAll([foo: "bar"])
    }

    @Test(expected=UnsupportedOperationException.class)
    void clearNotSupported() {
        new ColumnConstrainedMap([foo:"foobar", baz:"bar", foobar: null], ["bar", "baz", "foobar"] as Set).clear()
    }

    @Test
    void keySetReturnedHasKeysInMapAndColumnsSet() {
        def map = new ColumnConstrainedMap([foo:"foobar", baz:"bar", foobar: null], ["bar", "baz", "foobar"] as Set)
        assert 2 == map.keySet().size()
        assert map.keySet().contains("foobar")
        assert map.keySet().contains("baz")
    }

    @Test
    void withAllLowerAndAllUpperContainsKeyWillBeTrue() {
        def map = new ColumnConstrainedMap([foo:"foobar", baz:"bar", FOOBAR: "barfoo"], ["bar", "BAZ", "foobar"] as Set)
        assert map.containsKey("baz")
        assert map.containsKey("BAZ")
        assert map.containsKey("foobar")
        assert map.containsKey("FOOBAR")
    }
}
