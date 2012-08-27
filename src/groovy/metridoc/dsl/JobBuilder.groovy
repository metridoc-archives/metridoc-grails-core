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

import gant.ext.GantActivator
import groovy.util.logging.Slf4j
import metridoc.targets._CamelRouting
import metridoc.targets._PropertiesLoader
import metridoc.targets._DataSourceLoader
import metridoc.tools.BulkSql
import org.codehaus.gant.GantBinding
import org.slf4j.LoggerFactory
import metridoc.targets._SchemaLoader
import metridoc.targets._BaseUtilities

/**
 * Primary interface to run jobs.  Useful for embedding metridoc into standard java apps as well.  For
 * {@link JobBuilder} documentation see http://code.google.com/p/metridoc/wiki/JobBuilder
 *
 *
 */
@Slf4j
class JobBuilder {

    def Binding services = new Binding()

    public static final String DEFAULT_JOB = "default"
    public static final String METRIDOC_JOBS = "metridoc.jobs"

    def static void run(Closure closure) {
        run(DEFAULT_JOB, closure)
    }

    def static void run(String jobName, Closure closure) {
        JobBuilder.run([jobName] as String[], closure)
    }

    def static void run(String[] jobNames, Closure closure) {

        def runner = new ClosureRunner(closure: closure)
        runner.run()
        jobNames.each {
            runner."${it}".call()
        }
    }

    def static isJob(Script job) {
        job.services = job.binding
        def gantIsAlreadyLoaded = job.binding instanceof GantBinding

        def log = LoggerFactory.getLogger(JobBuilder.class)
        if (!gantIsAlreadyLoaded) {
            GantActivator.activateGant(job)

            log.debug("gant activated")
        }

        if (!job.binding.variables.containsKey("log")) {
            job.log = log
        }

        addPlugins(job)
    }

    def static addPlugins(Script job) {
        job.includeTargets << _PropertiesLoader
        job.includeTargets << _DataSourceLoader
        job.includeTargets << _CamelRouting
        job.includeTool << BulkSql
        job.includeTargets << _SchemaLoader
        job.includeTargets << _BaseUtilities
    }

    def static job() {
        def job = new Job()
        JobBuilder.isJob(job)
        return job
    }
}

class Job extends Script {

    @Override
    Object run() {
        //do nothing
    }
}

class ClosureRunner extends Script {
    Closure closure

    @Override
    Object run() {
        JobBuilder.isJob(this)
        closure.delegate = this
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.call()
    }
}