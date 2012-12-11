includeTargets << grailsScript("_GrailsInit")
includeTargets << new File("$metridocCorePluginDir/scripts/_DownloadMetridocFiles.groovy")

target(main: "deletes all unused files to make a cleaner project for creating job scripts only") {

    if(grailsAppName == "metridoc-core") {
        grailsConsole.error "cannot run configuration script in the metridoc-core plugin"
        exit(-1)
    }

    grailsConsole.info "deleting directories that are not needed for jobs"

    ant.delete(dir: "grails-app/conf/hibernate")
    ant.delete(dir: "grails-app/conf/spring")
    ant.delete(dir: "grails-app/conf/BootStrap.groovy")
    ant.delete(dir: "grails-app/conf/ApplicationResources.groovy")
    ant.delete(dir: "grails-app/controllers")
    ant.delete(dir: "grails-app/services")
    ant.delete(dir: "grails-app/utils")
    ant.delete(dir: "grails-app/domain")
    ant.delete(dir: "grails-app/i18n")
    ant.delete(dir: "grails-app/taglib")
    ant.delete(dir: "grails-app/views")
    ant.delete(dir: "grails-app/routes")
    ant.delete(dir: "grails-app/realms")

    ant.delete(dir: "lib")
    ant.delete(dir: "scripts")
    ant.delete(dir: "src")
    ant.delete(dir: "web-app/css")
    ant.delete(dir: "web-app/js")
    ant.delete(dir: "web-app/META-INF")
    def applicationPropertiesPath = "application.properties"

    def propertyFile = new File(applicationPropertiesPath)

    if (propertyFile.exists()) {
        Properties properties = new Properties()
        properties.load(propertyFile.newInputStream())
        properties.setProperty("metridoc.jobOnly", "true")
        def output = propertyFile.newOutputStream()
        def description = "Grails Metadata file"
        properties.store(output, description)
    }


    depends(downloadMetridocFiles)
}

setDefaultTarget(main)
