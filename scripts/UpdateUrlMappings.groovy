includeTargets << new File("$metridocCorePluginDir/scripts/_ConfigHelper.groovy")

target(main: "updates url mappings file") {
    depends(updateUrlMappings)
}

setDefaultTarget(main)
