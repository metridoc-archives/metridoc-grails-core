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
 * Date: 6/28/12
 * Time: 4:36 PM
 */
class ClassUtils {

    /**
     *
     * @param clazz
     * @param variableName
     *
     * @return the value of the static variable, null if class does not have the variable
     */
    def static getStaticVariable(clazz, String variableName) {
        try {
            clazz."${variableName}"
        } catch (MissingPropertyException me) {
            return null
        }
    }

    def static getStaticVariable(clazz, String variableName, defaultValue) {
        def result
        try {
            result = clazz."${variableName}"
        } catch (MissingPropertyException me) {
            result = defaultValue
        }

        return result
    }
}
