includeTargets << grailsScript("_GrailsPackage")
includeTargets << new File("$metridocCorePluginDir/scripts/_DownloadMetridocFiles.groovy")
includeTargets << new File("$metridocCorePluginDir/scripts/_MetridocJobUtils.groovy")

target(main: "deletes all unused files to make a cleaner project for creating job scripts only") {

    if(grailsAppName == "metridoc-core") {
        grailsConsole.error "cannot run configuration script in the metridoc-core plugin"
        exit(-1)
    }

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

    //delete default files that are not needed
    ant.sequential {
        delete(file:"scripts/_Install.groovy")
        delete(file:"scripts/_Uninstall.groovy")
        delete(file:"scripts/_Upgrade.groovy")
        delete(file:"grails-app/views/error.gsp")
    }

    depends(downloadMetridocFiles, packageApp, createMetridocJob)
}

setDefaultTarget(main)
