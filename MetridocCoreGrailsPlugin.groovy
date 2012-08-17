import metridoc.schema.SchemaRunner

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
    // the plugin version
    def version = "0.5"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "2.0.4 > *"
    // the other plugins this plugin depends on
    def loadAfter = ["rest-client-builder", "release", "hibernate"]

    // resources that are excluded from plugin packaging
    def pluginExcludes = [
        "scripts/_Events.groovy"
    ]

    def title = "Metridoc Core Plugin" // Headline display name of the plugin
    def author = "Thomas Barker"
    def authorEmail = "tbarker@pobox.upenn.edu"
    def description = '''\

    Provides core functionality for metridoc and all related plugins
'''

    def documentation = "http://metridoc.googlecode.com"

    def license = "ECL2"

    def organization = [ name: "University of Pennsylvania", url: "http://www.upenn.edu/" ]

//    def issueManagement = [ system: "JIRA", url: "metridoc.googlecode.com/svn/trunk/metridoc-core" ]

    def scm = [ url: "https:metridoc.googlecode.com/svn/trunk/metridoc-core" ]

    def doWithWebDescriptor = { xml ->
        // Implement additions to web.xml (optional), this event occurs before
    }

    def doWithSpring = {
        userSchemaRunner(SchemaRunner) {
            schema = "schemas/user/userSchema.xml"
            dataSource = ref("dataSource_admin")
        }

        adminSchemaRunner(SchemaRunner) {
            schema = "schemas/admin/adminSchema.xml"
            dataSource = ref("dataSource_admin")
        }
    }

    def doWithDynamicMethods = { ctx ->
        //Implement registering dynamic methods to classes (optional)
    }

    def doWithApplicationContext = { applicationContext ->
        // Implement post initialization spring config (optional)
    }

    def onChange = { event ->
        // Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    def onConfigChange = { event ->
        // Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }

    def onShutdown = { event ->
        // Implement code that is executed when the application shuts down (optional)
    }
}
