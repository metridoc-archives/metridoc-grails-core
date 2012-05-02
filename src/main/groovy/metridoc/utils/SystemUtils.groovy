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
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 7/30/11
 * Time: 10:16 AM
 *
 * Provides some useful system properties without having to remember the Java properties.  The functionality
 * essentially resembles the SystemUtils class in commons-io from Apache
 *
 */
class SystemUtils {

    public static final String FILE_SEPARATOR = getSystemProperty("file.separator");
    public static final String USER_HOME = getSystemProperty("user.home");
    public static final String USER_DIR = getSystemProperty("user.dir");
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    public static final String METRIDOC_HOME = System.getProperty("metridoc.home", "${USER_HOME}/.metridoc");

    //-----------------------------------------------------------------------
    /**
     * <p>Gets a System property, defaulting to <code>null</code> if the property
     * cannot be read.</p>
     *
     * <p>If a <code>SecurityException</code> is caught, the return
     * value is <code>null</code> and a message is written to <code>System.err</code>.</p>
     *
     * @param property the system property name
     * @return the system property value or <code>null</code> if a security problem occurs
     */
    private static String getSystemProperty(String property) {
        try {
            return System.getProperty(property);
        } catch (SecurityException ex) {
            // we are not allowed to look at this property
            System.err.println(
                "Caught a SecurityException reading the system property '" + property
                    + "'; the SystemUtils property value will default to null."
            );
            return null;
        }
    }
}
