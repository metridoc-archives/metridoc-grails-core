import grails.plugin.quartz2.QuartzFactoryBean
import metridoc.core.QuartzMonitorJobFactory
import metridoc.core.ScriptJobArtefactHandler
import metridoc.utils.BootupUtils
import org.apache.shiro.mgt.RememberMeManager
import org.apache.shiro.web.mgt.CookieRememberMeManager

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

    static DEFAULT_MAX_REMEMER_ME = 60 * 60 //one hour
    // the plugin version
    def version = "0.54.2"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "2.0.4 > *"

    def dependsOn = [quartz2: "0.2.3 > *"]
    // the other plugins this plugin depends on
    def loadAfter = ["rest-client-builder", "release", "hibernate", "quartz2", "resources"]

    def artefacts = [new ScriptJobArtefactHandler()]

    def watchedResources = [
    ]

    def pluginExcludes = [
            "grails-app/workflows/metridoc/test/**/*",
            "grails-app/controllers/metridoc/test/**/*",
            "grails-app/domain/metridoc/test/**/*",
            "grails-app/jobs/metridoc/test/**/*",
            "grails-app/conf/MetridocConfig.groovy"
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

    def doWithSpring = {


        def shiroConfig = application.config.security.shiro
        def mcfg = application.mergedConfig
        def quartzProps = loadQuartzConfig(application.mergedConfig)
        quartzScheduler(QuartzFactoryBean) {
            jobFactory = quartzJobFactory
            quartzProperties = quartzProps
            grailsApplication = ref('grailsApplication')
            autoStartup = false
            globalJobListeners = [ref('jobErrorLoggerListener')]//,ref('persistenceContextJobListener')]
            waitForJobsToCompleteOnShutdown = true
            if (mcfg.grails.plugin.quartz2.jdbcStore) {
                dataSource = ref('dataSource')
                transactionManager = ref('transactionManager')
            }
        }
        //have to do it in here instead of using the plugin config plugin since the shiro plugin does not use the
        //plugin config plugin
        BootupUtils.addDefaultShiroConfig(shiroConfig)

        quartzJobFactory(QuartzMonitorJobFactory) {
            sessionFactory = ref("sessionFactory")
        }

    }

    def doWithDynamicMethods = { ctx ->
        //Implement registering dynamic methods to classes (optional)
    }

    //adds the manual trigger to all jobs that do not have triggers
    def doWithApplicationContext = { applicationContext ->
        RememberMeManager manager = applicationContext.getBean("shiroRememberMeManager", RememberMeManager)
        if (manager instanceof CookieRememberMeManager) {
            manager.cookie.setMaxAge(DEFAULT_MAX_REMEMER_ME)
        }
    }

    static Properties loadQuartzConfig(config) {
        def properties = new Properties()
        if (config.org.containsKey('quartz')) {
            properties << config.org.quartz.toProperties('org.quartz')
        }

        //config.quartz._properties = properties

        return properties
    }
}
