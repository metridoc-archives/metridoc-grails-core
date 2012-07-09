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
package metridoc.annotations

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 5/17/12
 * Time: 11:44 AM
 */
@java.lang.annotation.Target([java.lang.annotation.ElementType.TYPE])
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Documented
public @interface ReportApplication {
    /**
     * the name of the report, by the default the class name will be used
     */
    String value() default ""
    /**
     * indicates the development stage of the report, only {@link Status#GA} reports are viewable
     * by normal candidates, everything else is only visable at GA
     */
    Status status() default Status.ALPHA
    /**
     * type of report, either indicates
     */
    Type type() default Type.REPORT

    String category() default ""
}

public enum Status {
    ALPHA,
    BETA,
    GA
}

public enum Type {
    REPORT,
    ADMIN
}