import org.apache.commons.io.FileUtils

updateFile = { String path, String url ->
    grailsConsole.info "replacing $path with $url"
    ant.delete(file: path)
    def file = new File(path)
    file << new URL(url).newInputStream()
}

includeTargets << grailsScript("_GrailsPackage")


metridocGoogleCodeSvn = "http://metridoc.googlecode.com/svn"
metridocGoogleCodeTrunk = "$metridocGoogleCodeSvn/trunk"
metridocMavenRepo = "$metridocGoogleCodeSvn/maven/repository"
metridocGoogleCodeCore = "$metridocGoogleCodeTrunk/metridoc-core"
metridocGoogleCodeConfigurationHome = "$metridocGoogleCodeCore/grails-app/conf"
grailsConfigurationHome = "grails-app/conf"

updateConfigDirFile = { String fileName ->
    updateFile("$grailsConfigurationHome/${fileName}.groovy", "$metridocGoogleCodeConfigurationHome/${fileName}.groovy")
}

target(init: "initializes the script utils variable") {
    depends(parseArguments, packageApp)
    ScriptUtils = classLoader.loadClass("metridoc.utils.ScriptUtils")
}

target(downloadMetridocFiles: "Downloads and replaces all relevant metridoc config files") {
    depends(updateConfigFile)
    def isPlugin = binding.hasVariable("isPlugin") ? binding.getVariable("isPlugin") : false

    grailsConsole.info "replacing the UrlMappings.groovy file"
    def urlMappingsPath = "grails-app/conf/UrlMappings.groovy"
    ant.delete(file: urlMappingsPath)
    def urlMappings = new File(urlMappingsPath)
    urlMappings << new URL("http://metridoc.googlecode.com/svn/trunk/metridoc-core/grails-app/conf/UrlMappings.groovy").newInputStream()
}

target(updateConfigFile: "updates the config file") {
    updateConfigDirFile("Config")
}

target(updateUrlMappings: "updates the url mappings file") {
    updateConfigDirFile("UrlMappings")
}

target(updateMetridocVersion: "updates the build config file") {
    depends(init)
    def version
    if (argsMap.includeSnapshots) {
        version = ScriptUtils.getMostRecentCoreVersion(true)
    } else {
        version = ScriptUtils.getMostRecentCoreVersion()
    }
    grailsConsole.info "updating metridoc-core version to $version"
    def buildConfig = new File("grails-app/conf/BuildConfig.groovy")
    def template = """
//TEMPLATE_METRIDOC_CORE_VERSION
        compile(':metridoc-core:$version')
        //TEMPLATE_METRIDOC_CORE_VERSION
"""
    ScriptUtils.updateWithTemplate("METRIDOC_CORE_VERSION", template as String, buildConfig)
}

target(overwriteBuildConfig: "overrites the current BuildConfig.groovy file") {
    def template = new File("$metridocCorePluginDir/src/templates/config/BuildConfigTemplate.groovy")
    def destination = new File("grails-app/conf/BuildConfig.groovy")
    FileUtils.copyFile(template, destination)
    depends(updateMetridocVersion)
}

target(overwriteConfig: "overwrites the config file with metridoc-core config template") {
    def template = new File("$metridocCorePluginDir/src/templates/config/ConfigTemplate.groovy")
    def config = new File("grails-app/conf/Config.groovy")
    FileUtils.copyFile(template, config)
}

target(overwriteUrlMappings: "overwrites the url mappings with metridoc-cores version") {
    def template = new File("$metridocCorePluginDir/src/templates/config/UrlMappingsTemplate.groovy")
    def config = new File("grails-app/conf/UrlMappings.groovy")
    FileUtils.copyFile(template, config)
}

target(deleteUnusedFiles: "deletes all view files and front end assets not being used") {
    ant.delete(dir: "grails-app/views")
    ant.delete(dir: "web-app/css")
    ant.delete(dir: "web-app/js")
    ant.delete(dir: "web-app/images")
}





