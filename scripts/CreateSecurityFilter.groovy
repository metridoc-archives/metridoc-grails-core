includeTargets << grailsScript("_GrailsArgParsing")
includeTargets << new File ("${shiroPluginDir}/scripts/_ShiroInternal.groovy")

target(main: "creates a shiro security filter") {
    depends(parseArguments, createSecurityFilters)
}

setDefaultTarget(main)
