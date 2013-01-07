includeTargets << grailsScript("_GrailsCreateArtifacts")

target(createMetridocJob: "creates a metridoc job based on the name of a passed parameter") {
    depends(checkVersion, parseArguments)

    def type = "MetridocJob"
    promptForName(type: type)

    def name = argsMap["params"][0]
    createArtifact(name: name, suffix: "Job", type: type, path: "grails-app/jobs")
}
