updateFile = {String path, String url ->
    grailsConsole.info "replacing $path with $url"
    ant.delete(file: path)
    def file = new File(path)
    file << new URL(url).newInputStream()
}

metridocGoogleCodeTrunk = "http://metridoc.googlecode.com/svn/trunk"
metridocGoogleCodeCore = "$metridocGoogleCodeTrunk/metridoc-core"
metridocGoogleCodeConfigurationHome = "$metridocGoogleCodeCore/grails-app/conf"
grailsConfigurationHome = "grails-app/conf"

updateConfigDirFile = {String fileName ->
    updateFile("$grailsConfigurationHome/${fileName}.groovy", "$metridocGoogleCodeConfigurationHome/${fileName}.groovy")
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

target(updateBuildConfig: "updates the build config file") {
    updateFile("$grailsConfigurationHome/BuildConfig.groovy", "$metridocGoogleCodeTrunk/startup/BuildConfig.groovy")
}



