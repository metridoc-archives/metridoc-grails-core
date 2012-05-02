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
package metridoc.console;

import org.junit.Test;

import java.lang.reflect.Method;

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 2/5/12
 * Time: 11:30 AM
 */
public class MetridocConsoleJavaInvokeTest {

    @Test
    public void testReflection() throws Exception {
        Class clazz = Class.forName("metridoc.console.MetridocConsole");
        Object instance = clazz.newInstance();
        Method method = clazz.getMethod("runConsoleCommand", String[].class);
        String[] methodArgument = new String[]{"testRun", "foo"};
        Object[] params = new Object[1];
        params[0] = methodArgument;
        method.invoke(instance, params);
    }
}
