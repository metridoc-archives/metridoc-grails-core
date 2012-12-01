import org.apache.commons.io.FileUtils

includeTargets << grailsScript("_GrailsInit")

target(main: "Adds configuration files for plugins or applications") {

    def confPath = "/grails-app/conf"
    def fromPath = "$metridocCorePluginDir$confPath"
    def toPath = "$basedir$confPath"

    copyFile(fromPath, toPath, "UrlMappings.groovy")
    copyFile(fromPath, toPath, "Config.groovy")
}

def copyFile(fromPath, toPath, fileName) {
    def from = new File("$fromPath/$fileName")
    def to = new File("$toPath/$fileName")
    if (from != to) {
        grailsConsole.info "adding  $from to $to"
        FileUtils.copyFile(from, to)
    } else {
        grailsConsole.info "$from and $to are exactly the same, you are probably running this commannd in metridoc-core"
    }
}

setDefaultTarget(main)
