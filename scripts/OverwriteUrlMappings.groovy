includeTargets << new File("$metridocCorePluginDir/scripts/_ConfigHelper.groovy")

target(main: "overwrites the current url mappings with the UrlMappings template") {
    depends(overwriteUrlMappings)
}

setDefaultTarget(main)
