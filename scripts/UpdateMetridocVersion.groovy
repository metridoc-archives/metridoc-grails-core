includeTargets << new File("$metridocCorePluginDir/scripts/_ConfigHelper.groovy")

target(main: "updates build config file") {
    depends(updateMetridocVersion)
}

setDefaultTarget(main)
