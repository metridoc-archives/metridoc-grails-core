includeTargets << grailsScript("_GrailsPackage")
includeTargets << new File("$metridocCorePluginDir/scripts/_ConfigHelper.groovy")
includeTargets << new File("$metridocCorePluginDir/scripts/_MetridocJobUtils.groovy")

target(main: "deletes all unused files to make a cleaner project for creating job scripts only") {

    if (grailsAppName == "metridoc-core") {
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

    depends(overwriteBuildConfig, overwriteConfig, overwriteUrlMappings, deleteUnusedFiles, createMetridocJob)
}

setDefaultTarget(main)
