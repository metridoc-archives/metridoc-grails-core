includeTargets << new File("$metridocCorePluginDir/scripts/_DownloadMetridocFiles.groovy")

target(main: "configures a metridoc application") {
    depends(updateBuildConfig, updateConfigFile, updateUrlMappings)
}

setDefaultTarget(main)