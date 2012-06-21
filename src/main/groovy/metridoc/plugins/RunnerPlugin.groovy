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

import metridoc.dsl.Job
import metridoc.dsl.JobBuilder
import metridoc.dsl.JobsHelper
import org.codehaus.gant.GantBinding

/**
 *
 * Allows a user to run an external job while passing the bindings and in some cases converting the script / class
 * into a job as well.  For more details please see <a href="http://code.google.com/p/metridoc/wiki/JobApiRunnerPlugin" target="_blank">http://code.google.com/p/metridoc/wiki/JobApiRunnerPlugin</a>
 */
@Plugin(category = "job")
class RunnerPlugin {
    def static runJob(Script self, Script script) {

        if (!(self.binding instanceof GantBinding)) {
            JobBuilder.isJob(self)
        }
        self.binding.variables.putAll(script.binding.variables)
        script.binding = self.binding
        JobBuilder.addPlugins(script)
        script.services = script.binding
        script.run()
    }

    def static runJob(Script self, Class<Script> clazz) {
        runJob(self, clazz.newInstance())
    }

    def static jobs(Script self, Closure closure) {
        def helper = new JobsHelper()
        helper.closure = closure
        helper.services = self.binding
        helper.run()
    }

    def static runJob(Script self) {
        runJob(self, "defaultJob")
    }

    def static runScript(Script self, Script scriptToRun) {
        def variables = self.binding.variables
        scriptToRun.binding.variables.putAll(variables)
        scriptToRun.run()
    }

    def static runJob(Script self, String jobName) {
        Job job
        try {
            job = self."${jobName}"
        } catch (MissingPropertyException ex) {
            throw new MissingJobException(jobName)
        }
        job.run()
    }

    def static runJobs(Script self, String... jobNames) {
        jobNames.each {
            runJob(self, it)
        }
    }
}
