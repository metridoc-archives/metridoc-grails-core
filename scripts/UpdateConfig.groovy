includeTargets << new File("$metridocCorePluginDir/scripts/_ConfigHelper.groovy")

target(main: "updates config file") {
    depends(updateConfigFile)
}

setDefaultTarget(main)
