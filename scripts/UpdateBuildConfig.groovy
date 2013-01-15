includeTargets << new File("$metridocCorePluginDir/scripts/_DownloadMetridocFiles.groovy")

target(main: "updates build config file") {
    depends(updateBuildConfig)
}

setDefaultTarget(main)
