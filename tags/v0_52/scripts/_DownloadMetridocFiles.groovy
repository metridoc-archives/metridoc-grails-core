
target(downloadMetridocFiles: "Downloads and replaces all relevant metridoc config files") {
    def isPlugin = binding.hasVariable("isPlugin") ? binding.getVariable("isPlugin") : false

    grailsConsole.info "replacing the UrlMappings.groovy file"
    def urlMappingsPath = "grails-app/conf/UrlMappings.groovy"
    ant.delete(file: urlMappingsPath)
    def urlMappings = new File(urlMappingsPath)
    urlMappings << new URL("http://metridoc.googlecode.com/svn/trunk/metridoc-core/grails-app/conf/UrlMappings.groovy").newInputStream()

    grailsConsole.info "replacing the Config.groovy file"
    def configPath = "grails-app/conf/Config.groovy"
    ant.delete(file: configPath)
    def config = new File(configPath)
    config << new URL("http://metridoc.googlecode.com/svn/trunk/metridoc-core/grails-app/conf/Config.groovy").newInputStream()
}
