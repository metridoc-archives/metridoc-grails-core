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
 * Date: 10/10/11
 * Time: 12:49 PM
 */
public class ColumnConstrainedListTest {

    @Test(expected = IllegalArgumentException.class)
    void testThatColumnsMustNotBeNull() {
        new ColumnConstrainedList([[foo: "bar"]], null)
    }

    @Test(expected = IllegalArgumentException.class)
    void testThatListMapMustNotBeNull() {
        new ColumnConstrainedList(null, ["foo"] as Set)
    }

    @Test(expected = IllegalArgumentException.class)
    void testThatColumnsMustNotBeEmpty() {
        new ColumnConstrainedList([[foo: "bar"]], [] as Set)
    }

    @Test(expected = IllegalArgumentException.class)
    void testThatListMapMustNotBeEmpty() {
        new ColumnConstrainedList([], ["foo"] as Set)
    }


}
