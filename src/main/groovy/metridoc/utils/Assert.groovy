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

package metridoc.utils;

/**
 *
 * @author Tommy Barker
 */
public class Assert {

    public static void notNull(Object object, String message) {
        testCondition(object != null, message);
    }

    public static void notEmpty(String text, String message) {
        notNull(text, message);
        testCondition(!StringUtils.EMPTY.equals(text), message);
    }

    public static void notEmpty(Object[] objects, String message) {
        testCondition(objects.length > 0, message);
    }

    public static void isTrue(boolean condition, String message) {
        testCondition(condition, message);
    }

    public static void notEmptyAndContentNotEmpty(Object[] objects, String message) {
        notNull(objects, message);
        notEmpty(objects, message);
        for (Object object : objects) {
            notNull(object, message);
            if (object instanceof String) {
                String text = (String) object;
                notEmpty(text, message);
            }
        }
    }

    static void testCondition(boolean condition, String message) {
        if (!condition) {
            throw new IllegalArgumentException(message);
        }
    }
}
