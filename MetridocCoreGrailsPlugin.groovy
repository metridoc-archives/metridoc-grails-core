import metridoc.utils.ShiroBootupUtils
import metridoc.workflows.WorkflowArtefactHandler
import org.apache.commons.lang.StringUtils
import org.codehaus.groovy.grails.commons.GrailsClass
import org.springframework.beans.factory.config.MethodInvokingFactoryBean
import metridoc.core.QuartzMonitorJobFactory
import grails.plugin.quartz2.QuartzFactoryBean

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
class MetridocCoreGrailsPlugin {

    def artefacts = [WorkflowArtefactHandler]
    // the plugin version
    def version = "0.52-SNAPSHOT"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "2.0.4 > *"

    def dependsOn = [quartz2: "0.2.3"]
    // the other plugins this plugin depends on
    def loadAfter = ["rest-client-builder", "release", "hibernate", "quartz2"]
    def loadBefore = ["shiro"]

    def watchedResources = [
        "file:./grails-app/workflows/**/*Workflow.groovy",
        "file:./plugins/*/grails-app/workflows/**/*Workflow.groovy",
    ]

    def pluginExcludes = [
        "scripts/_Events.groovy",
        "grails-app/workflows/metridoc/test/**/*",
        "grails-app/controllers/metridoc/test/**/*",
        "grails-app/domain/metridoc/test/**/*",
        "grails-app/jobs/metridoc/test/**/*"
    ]

    def title = "Metridoc Core Plugin" // Headline display name of the plugin
    def author = "Thomas Barker"
    def authorEmail = "tbarker@pobox.upenn.edu"
    def description = '''\

    Provides core functionality for metridoc and all related plugins
'''

    def documentation = "http://metridoc.googlecode.com"

    def license = "ECL2"

    def organization = [name: "University of Pennsylvania", url: "http://www.upenn.edu/"]

//    def issueManagement = [ system: "JIRA", url: "metridoc.googlecode.com/svn/trunk/metridoc-core" ]

    def scm = [url: "https:metridoc.googlecode.com/svn/trunk/metridoc-core"]

    def doWithWebDescriptor = { xml ->
        // Implement additions to web.xml (optional), this event occurs before
    }



    def doWithSpring = {

        def workflowClasses = application.workflowClasses

        workflowClasses.each {GrailsClass workflowClass ->
            configureWorkflowBeans.delegate = delegate
            configureWorkflowBeans(workflowClass)
        }

        def shiroConfig = application.config.security.shiro

        def disableQuartz = Boolean.valueOf(System.getProperty("metridoc.quartz.disabled", "false"))
        if(disableQuartz) {
            def quartzProps = loadQuartzConfig(application.mergedConfig)
            quartzScheduler(QuartzFactoryBean) {
                quartzProperties = quartzProps
                grailsApplication = ref('grailsApplication')
                autoStartup = false
                globalJobListeners = [ref('jobErrorLoggerListener')]//,ref('persistenceContextJobListener')]
            }
        }
        //have to do it in here instead of using the plugin config plugin since the shiro plugin does not use the
        //plugin config plugin
        ShiroBootupUtils.addDefaultParameters(shiroConfig)

        quartzJobFactory(QuartzMonitorJobFactory) {
            sessionFactory = ref("sessionFactory")
        }
    }

    def doWithDynamicMethods = { ctx ->
        //Implement registering dynamic methods to classes (optional)
    }

    def doWithApplicationContext = { applicationContext ->

    }

    def onChange = { event ->
        if (application.isArtefactOfType(WorkflowArtefactHandler.TYPE, event.source)) {
            def context = event.ctx
            def scheduler = context?.getBean("quartzScheduler")
            def quartzService = context?.getBean("quartzService")
            // get quartz scheduler
            if (context && scheduler) {
                // if job already exists, delete it from scheduler
                //just stop all jobs
                scheduler.clear()

                // add job artefact to application
                def workflowClass = application.addArtefact(WorkflowArtefactHandler.TYPE, event.source)

                def shortName = StringUtils.uncapitalise(workflowClass.shortName)
                def beans = beans {
                    configureWorkflowBeans.delegate = delegate
                    configureWorkflowBeans(workflowClass)
                }

                context.registerBeanDefinition("${shortName}Class", beans.getBeanDefinition("${shortName}Class"))
                context.registerBeanDefinition("${shortName}", beans.getBeanDefinition("${shortName}"))

                quartzService.scheduleJobs()
            } else {
                log.error("Application context or Quartz Scheduler not found. Can't reload Quartz plugin.")
            }
        }
    }

    def configureWorkflowBeans = {workflowClass ->
        if (!workflowClass.isAbstract()) {
            def fullName = workflowClass.fullName
            def shortName = StringUtils.uncapitalise(workflowClass.shortName)

            "${shortName}Class"(MethodInvokingFactoryBean) {
                targetObject = ref("grailsApplication", true)
                targetMethod = "getArtefact"
                arguments = [WorkflowArtefactHandler.TYPE, fullName]
            }

            "${shortName}"(ref("${shortName}Class")) { bean ->
                bean.scope = "prototype"
                bean.factoryMethod = "newInstance"
                bean.autowire = "byName"
            }
        }
    }

    def onConfigChange = { event ->
        // Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }

    def onShutdown = { event ->
        // Implement code that is executed when the application shuts down (optional)
    }

    Properties loadQuartzConfig(config) {
        def properties = new Properties()
        if (config.org.containsKey('quartz')) {
            properties << config.org.quartz.toProperties('org.quartz')
        }

        //config.quartz._properties = properties

        return properties
    }
}
