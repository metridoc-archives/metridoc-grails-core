includeTargets << new File("$metridocCorePluginDir/scripts/_DownloadMetridocFiles.groovy")

target(main: "updates config file") {
    depends(updateConfigFile)
}

setDefaultTarget(main)
