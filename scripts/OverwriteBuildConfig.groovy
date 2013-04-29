includeTargets << new File("$metridocCorePluginDir/scripts/_ConfigHelper.groovy")

target(main: "overwrites the current build config with the BuildConfig template") {
    depends(overwriteBuildConfig)
}

setDefaultTarget(main)
