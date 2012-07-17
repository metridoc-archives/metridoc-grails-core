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

    grailsConsole.updateStatus "You must exit the grails shell before using this project again"
}

setDefaultTarget(main)
