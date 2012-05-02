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

/**
 *
 * Very similar to {@link Assert}, but throws an {@link IllegalStateException} instead of {@link IllegalArgumentException}
 */
class AssertState {

    public static void notNull(Object object, String message) {
        checkCondition() {
            Assert.notNull(object, message)
        }
    }

    public static void notEmpty(String text, String message) {
        checkCondition() {
            Assert.notEmpty(text as String, message) //using 'as' removes some annoying ambiguity error
        }
    }

    public static void isTrue(boolean condition, String message) {
        checkCondition() {
            Assert.isTrue(condition, message)
        }
    }

    private static void checkCondition(Closure check) {
        try {
            check()
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException(ex.getMessage())
        }
    }


}
