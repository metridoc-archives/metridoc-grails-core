includeTargets << new File("$metridocCorePluginDir/scripts/_ConfigHelper.groovy")

target(main: "updates config file") {
    throw new UnsupportedOperationException("updateConfig is not currently supported")
}

setDefaultTarget(main)
