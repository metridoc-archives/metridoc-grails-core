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

import metridoc.utils.Assert

/**
 * Runs the provided closure when running an embedded job with {@link metridoc.dsl.JobBuilder#run}
 */
class JobsHelper {
    Closure closure
    Binding services

    def run() {
        closure.setDelegate(this)
        closure.call()
    }

    void job(String name, Closure closure) {
        def job = new Job(closure: closure, binding: services)
        services[name] = job
        JobBuilder.isJob(job)
    }

    def methodMissing(String name, args) {
        Assert.notEmpty(args, "method ${name} is not legal in job dsl with args ${args}")

        if (!isJob(args)) {
            throw new MissingMethodException(name, JobsHelper.class, args)
        }

        if (args.size() == 1) {
            def job = new Job(closure: args[0], binding: services)
            services[name] = job
            JobBuilder.isJob(job)
        } else {
            def job = new Job(args: args[0], closure: args[1], binding: services)
            services[name] = job
            JobBuilder.isJob(job)
        }
    }

    private boolean isJob(args) {
        if (args.size() == 1) {
            return args[0] instanceof Closure
        }
        if (args.size() == 2) {
            return args[0] instanceof LinkedHashMap && args[1] instanceof Closure
        }

        return false
    }
}
