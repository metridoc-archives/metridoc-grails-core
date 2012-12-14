includeTargets << grailsScript("_GrailsCompile")

target(main: "Deletes all cached dependencies") {
    depends(parseArguments)
    def toDelete = []
    new File(grailsWorkDir).parentFile.eachDirRecurse {
        if (it.name.contains("org.grails.plugins")) {
            toDelete.add(it)
        }

        if(it.name.equals(grailsAppName) && it.parentFile.name.equals("projects")) {
            toDelete.add(it)
        }
    }

    toDelete.each {File file ->
        ant.delete(dir: file)
    }

    grailsConsole.info "since caches can get corrupt, we will exit the grails shell if we are in interactive mode"
    grailsConsole.info "please run grails --refresh-dependencies compile to re-download all dependencies"
    System.exit(0)
}

setDefaultTarget(main)
