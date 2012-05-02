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
package metridoc.console

/**
 *
 * A console abstraction that allows the user to run a job from a jar that contains a job and all of its dependencies.
 * Assuming a comprehensive jar, running a job via this console abstraction would be:
 * </br>
 * </br>
 * <code><pre>java -jar &lt;job jar&gt;.jar runJob &lt;jobName&gt;</code></pre>
 *
 *
 */
class RunJob extends Script {

    @Override
    Object run() {
        def job = args[1]
        def classLoader = Thread.currentThread().contextClassLoader
        classLoader.loadClass(job).newInstance().run()
    }
}

