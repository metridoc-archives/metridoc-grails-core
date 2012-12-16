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
package metridoc.scripts.helpers

import org.codehaus.gant.GantState

def baseDir = "${baseFile.path}/"
def projectPath = "${baseDir}/grails-app/"
def templatePath = "${metridocPluginPath}/templates/artifacts/"

def serviceAndControllerProcessing = {
    def projectName = file.name

    if (file.isDirectory()) {
        result.add(projectName)
    }
}

def viewsAndWebAppProcessing = {
    def notLayout = "layout" == file.name
    def fileIsProjectScript = file.isDirectory() && notLayout
    if (fileIsProjectScript) {
        result.add file.name
    }
}

def deleteNonHiddenFiles = {String filePath ->
    if (new File(filePath).exists()) {
        def hidden = []
        def files = []
        new File(filePath).eachFileRecurse {
            if (it.name.startsWith(".")) {
                hidden.add(it.name)
            }

            if (it.isFile()) {
                files.add(it)
            }
        }

        files.each {file ->
            def isHidden = false
            hidden.each { hiddenName ->
                if (file.path.contains(hiddenName)) {
                    isHidden = true
                }
            }

            if (!isHidden) {
                assert file.delete(): "could not delete file ${it}"
            }
        }
    }
}

def serviceControllerAndViewsCreateTemplates = {

    deleteNonHiddenFiles("${templateDir}/${projectName}")
    ant.copy(todir: "$templateDir$projectName") {
        fileset(dir: "$projectDir$projectName")
    }
}

def deleteDevelopmentTemplatesForControllersServicesViewsAndWebApp = {
    deleteNonHiddenFiles("${projectDir}/${projectName}")
}

def serviceControllerAndViewsInstallTemplates = {
    if (new File("${templateDir}/${projectName}").exists()) {
        ant.copy(todir: "$projectDir$projectName") {
            fileset(dir: "$templateDir$projectName")
        }
    }
}

projectDescription = [
    "controllers": [
        projectDir: "${projectPath}controllers/metridoc/",
        templateDir: "${templatePath}controllers/metridoc/",
        findProjects: serviceAndControllerProcessing,
        createTemplates: serviceControllerAndViewsCreateTemplates,
        installTemplates: serviceControllerAndViewsInstallTemplates,
        deleteDevTemplates: deleteDevelopmentTemplatesForControllersServicesViewsAndWebApp
    ],
    "services": [
        projectDir: "${projectPath}services/metridoc/",
        templateDir: "${templatePath}services/metridoc/",
        findProjects: serviceAndControllerProcessing,
        createTemplates: serviceControllerAndViewsCreateTemplates,
        installTemplates: serviceControllerAndViewsInstallTemplates,
        deleteDevTemplates: deleteDevelopmentTemplatesForControllersServicesViewsAndWebApp
    ],
    "views": [
        projectDir: "${projectPath}views/",
        templateDir: "${templatePath}views/",
        findProjects: viewsAndWebAppProcessing,
        createTemplates: serviceControllerAndViewsCreateTemplates,
        installTemplates: serviceControllerAndViewsInstallTemplates,
        deleteDevTemplates: deleteDevelopmentTemplatesForControllersServicesViewsAndWebApp
    ],
    "scripts": [
        projectDir: "${metridocPluginPath}/scripts/",
        templateDir: "${templatePath}scripts/",

        findProjects: {
            def matcher = (file.name =~ /^MDRun(.*).groovy$/)
            def fileIsProjectScript = file.isFile() && (matcher.find())
            if (fileIsProjectScript) {
                result.add matcher.group(1)
            }
        },

        createTemplates: {
            def scriptName = "MdRun${projectName.capitalize()}.groovy"
            def scriptFilePath = "${projectDir}${scriptName}"
            def scriptFile = new File(scriptFilePath)
            def templateFilePath = "${templateDir}${scriptName}"
            def templateFile = new File(templateFilePath)

            if (templateFile.exists()) {
                ant.delete(file: templateFilePath)
            }

            if (scriptFile.exists()) {
                ant.copy(file: scriptFilePath, tofile: templateFilePath)
            }
        },

        installTemplates: {
            def scriptName = "MdRun${projectName.capitalize()}.groovy"
            def scriptFilePath = "${projectDir}${scriptName}"
            def templateFilePath = "${templateDir}${scriptName}"

            if (new File(templateFilePath).exists()) {
                ant.copy(file: templateFilePath, tofile: scriptFilePath)
            }
        },

        deleteDevTemplates: {
            def scriptName = "MdRun${projectName.capitalize()}.groovy"
            def scriptFilePath = "${projectDir}${scriptName}"
            def scriptFile = new File(scriptFilePath)

            if (scriptFile.exists()) {
                ant.delete(file: scriptFilePath)
            }
        }
    ],
    "web-app": [
        projectDir: "${metridocPluginPath}/web-app/",
        templateDir: "${templatePath}web-app/",
        findProjects: viewsAndWebAppProcessing,
        createTemplates: serviceControllerAndViewsCreateTemplates,
        installTemplates: serviceControllerAndViewsInstallTemplates,
        deleteDevTemplates: deleteDevelopmentTemplatesForControllersServicesViewsAndWebApp
    ]
]

getFilesForProjectByDirectory = {String projectName, String directoryPath ->
    def directory = new File(metridocPluginPath + "/" + directoryPath)
    assert directory.exists(): "project ${projectName} does not exist, searched ${directory}"
    assert directory.isDirectory(): "project ${projectName} is not a directory"
    def result = []
    directory.eachFile {
        if (it.isFile()) {
            result.add(it)
        }
    }

    return result
}

getProjectFiles = {String projectName ->
    def result = []
    def artifactDir = "templates/artifacts/"

    ["controllers/metridoc/", "services/metridoc/", "views/"].each {
        result.addAll getFilesForProjectByDirectory(projectName, "${artifactDir}${it}${projectName}")
    }
    ["/images", "/css", "/js"].each {
        result.addAll getFilesForProjectByDirectory(projectName, "${artifactDir}web-app/${projectName}${it}")
    }
    return result
}

createProjectTemplates = {String projectName ->
    projectDescription.each {key, value ->
        def createTemplates = value.createTemplates
        if (createTemplates) {

            createTemplates.delegate = [
                templateDir: value.templateDir,
                projectDir: value.projectDir,
                projectName: projectName,
                ant: ant
            ]

            createTemplates.resolveStrategy = Closure.DELEGATE_FIRST
            createTemplates.call()
        }
    }
}

installProjectTemplates = {String projectName ->
    projectDescription.each {key, value ->
        def installTemplates = value.installTemplates
        if (installTemplates) {

            installTemplates.delegate = [
                templateDir: value.templateDir,
                projectDir: value.projectDir,
                projectName: projectName,
                ant: ant
            ]

            installTemplates.resolveStrategy = Closure.DELEGATE_FIRST
            installTemplates.call()
        }
    }
}

deleteDevProjectTemplates = {String projectName ->
    projectDescription.each {key, value ->
        def deleteDevTemplates = value.deleteDevTemplates
        if (deleteDevTemplates) {

            deleteDevTemplates.delegate = [
                templateDir: value.templateDir,
                projectDir: value.projectDir,
                projectName: projectName,
                ant: ant
            ]

            deleteDevTemplates.resolveStrategy = Closure.DELEGATE_FIRST
            deleteDevTemplates.call()
        }
    }
}

target(setupLogging: "sets a reasonable logging level") {
    GantState.verbosity = GantState.NORMAL
    ant.logger.setMessageOutputLevel(GantState.NORMAL)
}

target(doInstallProjectTemplates: "installs a project template into the project structure for development") {
    depends(parseArguments)

    def projectTemplatesToInstall = argsMap.params
    assert projectTemplatesToInstall: "at least one project must be provided"

    projectTemplatesToInstall.each {
        ant.echo(message: "installing project templates for ${it}")
        installProjectTemplates(projectTemplatesToInstall)
    }
}

target(doCreateProjectTemplates: "creates the project templates based on files in development") {
    depends(parseArguments)

    def projectsInDevelopment = getMetridocDevelopmentProjects()
    if (argsMap.containsKey("l")) {
        println "Available projects to install are:"
        projectsInDevelopment.each {
            println "    $it"
        }
    } else {
        def projectTemplatesToCreate = argsMap.params
        assert projectTemplatesToCreate: "at least one project must be provided"
        projectTemplatesToCreate.each {
            assert projectsInDevelopment.contains(it): "project ${it} is not in development"
        }

        projectTemplatesToCreate.each {
            ant.echo(message: "installing project templates for ${it}")
            createProjectTemplates(it)
        }
    }
}

target(doDeleteDevTemplates: "deletes all development templates") {
    depends(parseArguments)

    def projectTemplatesToInstall = argsMap.params
    assert projectTemplatesToInstall: "at least one project must be provided"

    projectTemplatesToInstall.each {
        ant.echo(message: "deleting project templates for ${it}")
        deleteDevProjectTemplates(it)
    }
}

processIfDirectoryExists = {String directory, Set result, Closure closure ->
    def projectDirectory = new File(directory)
    if (projectDirectory.exists() && projectDirectory.isDirectory()) {
        projectDirectory.listFiles().each {file ->
            def notHidden = !file.name.startsWith(".")
            if (notHidden) {
                closure.delegate = [file: file, result: result]
                closure.resolveStrategy = Closure.DELEGATE_FIRST
                closure.call()
            }
        }
    }
}

getMetridocDevelopmentProjects = {
    Set result = []
    projectDescription.each {key, value ->
        processIfDirectoryExists(value.projectDir, result, value.findProjects)
    }

    return result
}

