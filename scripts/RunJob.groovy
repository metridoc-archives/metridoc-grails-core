includeTargets << grailsScript("_GrailsBootStrap")

target(main: "The description of the script goes here!") {
    depends(parseArguments, packageApp)

    def restBuilder = classLoader.loadClass("grails.plugins.rest.client.RestBuilder").newInstance()
    def url = "http://localhost:8080/${grailsAppName}/quartz/runNow?jobGroup=DEFAULT&jobName=${argsMap.params[0]}&target=${argsMap.target}&returnTriggerId"
    def triggerId = restBuilder.get(url){
        auth("admin", "password")
    }.text
    grailsConsole.info "triger id is: $triggerId"
}



setDefaultTarget(main)
