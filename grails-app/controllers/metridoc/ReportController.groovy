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
package metridoc

import metridoc.utils.ClassUtils
import org.apache.commons.lang.StringUtils

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 6/29/12
 * Time: 11:19 AM
 */
abstract class ReportController {

    def static final CONTROLLER = "Controller"
    def static final WEBINF_VIEW_LOCATION = "/WEB-INF/grails-app/views"

    def index() {

        def model = getModel()
        model.reportName = ClassUtils.getStaticVariable(
            this.getClass(),
            "reportName",
            this.getClass().getName()
        )

        model.descriptionExists = descriptionTemplateExists()
        model.descriptionTemplate = getDescriptionTemplateLocation()
        model.templateDir = getTemplateDir()
        model.pluginName = pluginName()

        log.info("rendering report ${model.reportName} with model: ${model}")
        render(view: '/reports/index', model: model)
    }

    def getModel() {
        return [:]
    }

    def descriptionTemplateExists() {
        def templateLocation = getDescriptionTemplateLocation()
        def template = grailsAttributes.getTemplateUri(templateLocation, request)
        def resource = grailsAttributes.pagesTemplateEngine.getResourceForUri(template)

        //TODO: remove duplicate code here
        if (!resource.exists()) {
            templateLocation = "${WEBINF_VIEW_LOCATION}${templateLocation}"
            template = grailsAttributes.getTemplateUri(templateLocation, request)
            resource = grailsAttributes.pagesTemplateEngine.getResourceForUri(template)
        }

        log.info("checking if template location [${resource}] exists, resource exists? ${resource.exists()}")

        return resource && resource.exists() && resource.file
    }

    def getTemplateDir() {
        return "/reports/${controllerNameWithoutController()}"
    }

    def getDescriptionTemplateLocation() {

        return "${getTemplateDir()}/description"
    }

    def pluginName() {
        def pluginManager = applicationContext.pluginManager
        def name = controllerNameWithoutController()
        def pluginName = "metridoc${name.capitalize()}"
        def names = [] as Set
        pluginManager.allPlugins.each {
            names.add(it.name)
        }

        if (names.contains(pluginName)) {
            return pluginName
        }

        return null
    }

    def controllerNameWithoutController() {
        def simpleClassName = this.getClass().getSimpleName()
        def name = StringUtils.uncapitalise(simpleClassName)
        def nameWithNoController = name

        if (name.endsWith(CONTROLLER)) {
            def index = name.lastIndexOf(CONTROLLER)
            nameWithNoController = name.substring(0, index)
        }

        return nameWithNoController
    }
}
