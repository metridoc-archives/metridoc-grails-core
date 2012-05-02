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
package metridoc.dsl

/**
 * A self contained job that is used by the {@link metridoc.plugins.RunnerPlugin}.  Essentially allows a user to call
 * a Job later on.
 *
 * @see metridoc.dsl.JobsHelper
 * @see metridoc.plugins.RunnerPlugin
 *
 */
class Job extends Script {
    Closure closure
    Map args

    Binding getServices() {
        return binding
    }

    @Override
    Object run() {
        if (args && args.dependsOn) {
            Job dependency = binding."${args.dependsOn}"
            dependency.run()
        }
        closure.setDelegate(this)
        closure.setDirective(Closure.DELEGATE_FIRST)
        closure.setResolveStrategy(Closure.DELEGATE_FIRST)
        return closure.call()
    }
}
