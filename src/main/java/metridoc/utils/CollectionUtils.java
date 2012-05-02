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

import java.util.*;


/**
 * Inspired by the google guava framework
 *
 * @author Tommy Barker
 */
public class CollectionUtils {

    public static class Lists {
        public static <T> List<T> createArrayList() {
            return new ArrayList<T>();
        }
    }

    public static class Maps {

        public static <K, V> Map<K, V> createHashMap() {
            return new HashMap<K, V>();
        }

        public static <K, V> TreeMap<K, V> createTreeMap() {
            return new TreeMap<K, V>();
        }
    }

    public static class Sets {
        public static <T> Set<T> createHashSet() {
            return new HashSet<T>();
        }

        public static <T> Set<T> createTreeSet() {
            return new TreeSet<T>();
        }
    }

    public static String toString(Object[] array) {
        StrBuilder builder = new StrBuilder();
        builder.append("[");
        for (Object object : array) {
            builder.append(object.toString()).append(", ");
        }

        builder.chop().chop();
        builder.append("]");
        return builder.toString();
    }
}
