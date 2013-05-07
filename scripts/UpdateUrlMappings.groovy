includeTargets << new File("$metridocCorePluginDir/scripts/_ConfigHelper.groovy")

target(main: "updates url mappings file") {
    throw new UnsupportedOperationException("updateUrlMappings is not currently supported")
}

setDefaultTarget(main)
