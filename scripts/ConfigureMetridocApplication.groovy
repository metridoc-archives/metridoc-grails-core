includeTargets << new File("$metridocCorePluginDir/scripts/_ConfigHelper.groovy")

target(main: "configures a metridoc application") {
    depends(overwriteBuildConfig, overwriteConfig, overwriteUrlMappings, deleteUnusedFiles)
}

setDefaultTarget(main)