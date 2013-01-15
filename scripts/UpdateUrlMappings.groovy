includeTargets << new File("$metridocCorePluginDir/scripts/_DownloadMetridocFiles.groovy")

target(main: "updates url mappings file") {
    depends(updateUrlMappings)
}

setDefaultTarget(main)
