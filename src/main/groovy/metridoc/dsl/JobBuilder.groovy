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
import metridoc.plugins.PluginDB
import org.apache.tools.ant.Project
import org.slf4j.LoggerFactory

/**
 * Primary interface to run jobs.  Useful for embedding metridoc into standard java apps as well.  For
 * {@link JobBuilder} documentation see http://code.google.com/p/metridoc/wiki/JobBuilder
 *
 *
 */
class JobBuilder {

    def Binding services = new Binding()
    def log = LoggerFactory.getLogger("${METRIDOC_JOBS}.builder")

    public static final String DEFAULT_JOB = "defaultJob"
    public static final String METRIDOC_JOBS = "metridoc.jobs"
    public static final String METRIDOC_BUILDER = "${METRIDOC_JOBS}.builder"

    def static void run(Map args = null, Closure closure) {
        run(args, DEFAULT_JOB, closure)
    }

    def static void run(Map args = null, String jobName, Closure closure) {
        JobBuilder.run(args, [jobName] as String[], closure)
    }

    def static void run(Map args = null, String[] jobNames, Closure closure) {
        def binding = new Binding()
        def helper = new JobsHelper(closure: closure, services: binding)

        if (args) {
            args.each {key, value ->
                binding."${key}" = value
            }
        }

        helper.run()

        jobNames.each {
            binding."${it}".run()
        }
    }

    def static void jobs(LinkedHashMap args = null, Closure closure) {
        jobs(args, DEFAULT_JOB, closure)
    }

    def static void jobs(LinkedHashMap args = null, String jobName, Closure closure) {
        run(args, jobName, closure)
    }

    def static isJob(Script job) {
        job.services = job.binding
        /*
            TODO: make this a plugin instead maybe?
            Might not be possible since the activator needs to happen first in order to setup logging and avoid
            the slf4j message
         */
        GantActivator.activateGant(job)

        LoggerFactory.getLogger(JobBuilder.class).debug("gant activated")
        //TODO:add functionality when the plugin is a script as opposed to a mixin
        PluginDB.getInstance().getPlugins("job").each {Class plugin ->
            job.metaClass.mixin(plugin)
        }
    }

    static Script job(Binding binding = null) {
        def job = new Job()
        job.closure = {-> /* do nothing */}
        isJob(job)

        return job
    }

    /**
     *
     * Ant's {@link Project} and Groovy's {@link Binding} both use a repository to store references
     * to variables. This combines their functionality, allowing crud operations of their references
     * o be in sync
     *
     * @param binding
     * @param project
     */
    static void bindProjectAndBinding(Binding binding, Project project) {
        binding.variables.each {String key, value ->
            project.addReference(key, value)
        }

        binding.variables.putAll(project.getReferences())
        def oldSetVariable = binding.metaClass.getMetaMethod("setVariable", [String, Object] as Class[])
        def oldAddReference = project.metaClass.getMetaMethod("addReference", [String, Object] as Class[])
        project.metaClass.addReference = {String name, Object value ->
            oldSetVariable.invoke(binding, name, value)
            oldAddReference.invoke(project, name, value)
        }

        binding.metaClass.setVariable = {String name, Object value ->
            project.addReference(name, value)
        }
    }
}


