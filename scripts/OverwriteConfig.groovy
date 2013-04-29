includeTargets << new File("$metridocCorePluginDir/scripts/_ConfigHelper.groovy")

target(main: "overwrites the current config with the Config template") {
    depends(overwriteConfig)
}

setDefaultTarget(main)
