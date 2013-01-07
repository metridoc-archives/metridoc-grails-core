includeTargets << grailsScript("_GrailsCreateArtifacts")

target(createMetridocJob: "creates a metridoc job based on the name of a passed parameter") {
    depends(checkVersion, parseArguments)

    def type = "Job"
    promptForName(type: type)

    def name = argsMap["params"][0]
    createArtifact(name: name, suffix: type, type: type, path: "grails-app/jobs", templatePath: "$metridocCorePluginDir/templates/artifacts/metridoc")
}
