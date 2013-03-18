import java.util.regex.Matcher

includeTargets << grailsScript("_GrailsInit")

target(updateLogConfig: "Updates log config") {
    def metridocCoreConfigFile = new File("$metridocCorePluginDir/src/templates/config/ConfigLogTemplate.groovy")
    def localConfigFile = new File("$basedir/grails-app/conf/Config.groovy")
    def metridocCoreConfigContent = metridocCoreConfigFile.text
    def m = metridocCoreConfigContent =~ "(?s)TEMPLATE_LOG_4J.*?TEMPLATE_LOG_4J"
    m.find()
    String replacementText = m.group(0)
    newLocalConfigFileContent = localConfigFile.text.replaceAll("(?s)TEMPLATE_LOG_4J.*?TEMPLATE_LOG_4J", Matcher.quoteReplacement(replacementText))
    localConfigFile.delete()
    localConfigFile.write(newLocalConfigFileContent)
}

setDefaultTarget(updateLogConfig)
