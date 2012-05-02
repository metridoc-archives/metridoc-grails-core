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
package metridoc.utlis

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 2/20/12
 * Time: 2:38 PM
 */
class ClosureUtils {
    public static final String CLOSURE_CANNOT_BE_NULL = "closure cannot be null"
    public static final EXPECTED_TYPES_ARE_WRONG = {Closure closure, Class[] expectedTypes ->
        return "was expecting a closure that can process ${expectedTypes}, but the closure processes ${closure.getParameterTypes()}"
    }
    protected final static Set CHECKED_CLOSURES = []

    private ClosureUtils() {}

    static void validate(Closure closure, Class[] expectedTypes) {
        if (!CHECKED_CLOSURES.contains(closure)) {
            assert closure: CLOSURE_CANNOT_BE_NULL
            boolean singleObjectParam = closure.parameterTypes == [Object]
            boolean paramsMatch = expectedTypes == closure.parameterTypes
            assert singleObjectParam || paramsMatch: EXPECTED_TYPES_ARE_WRONG(closure, expectedTypes)

            CHECKED_CLOSURES.add(closure)
        }
    }
}
