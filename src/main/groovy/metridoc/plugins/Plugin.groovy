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
package metridoc.plugins

import java.lang.annotation.*

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Plugin {
    /**
     * specifies the category of the plugin.  For instance 'job'.  With category and name, you can basically construct
     * a namespace for the plugin.  Database extension points would find this useful, basically swapping in database
     * implementations where the name dictates the database provider
     *
     * @return the type of plugin
     */
    String category()
    /**
     * The name of the plugin.  Combined with the {@link Plugin#category}, one can create a namespace for the plugin.
     * If the {@link Plugin#category} is enough (such as a job plugin), then this parameter is not needed
     *
     * @return
     */
    String name() default ""
}