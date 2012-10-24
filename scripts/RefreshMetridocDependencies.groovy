includeTargets << grailsScript("_GrailsCompile")

target(main: "Deletes the cached metridoc dependencies") {
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

    grailsConsole.info "since caches can get currupt, we will exit the grails shell if we are in interactive mode"
    System.exit(0)
}

setDefaultTarget(main)
