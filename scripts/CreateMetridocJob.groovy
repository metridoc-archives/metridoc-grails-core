includeTargets << grailsScript("_GrailsInit")
includeTargets << new File("$metridocCorePluginDir/scripts/_MetridocJobUtils.groovy")

target(main: "creates a metridoc job based on the name of a passed parameter") {
    depends(createMetridocJob)
}

setDefaultTarget(main)
