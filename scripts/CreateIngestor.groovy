includeTargets << grailsScript("_GrailsInit")
includeTargets << grailsScript("_GrailsCreateArtifacts")

target(main: "creates an ingestor from a template") {
    depends(checkVersion, parseArguments)

    def type = "Ingestor"
    promptForName(type: type)

    def name = argsMap["params"][0]
    createArtifact(name: name, suffix: type, type: type, path: "grails-app/ingestors")
}

setDefaultTarget(main)
