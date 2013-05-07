eventPackagingEnd = { msg ->

    Properties properties = new Properties()
    properties.load(new File("application.properties").newInputStream())
    def jobOnly = properties.'metridoc.jobOnly' ? Boolean.valueOf(properties.getProperty("metridoc.jobOnly")) : false
    if (jobOnly) {
        deleteDir = { String path ->
            def directory = new File(path)
            if (!directory.listFiles()) {
                ant.delete(dir: path)
            }
        }

        deleteDir("grails-app/controllers")
        deleteDir("grails-app/services")
        deleteDir("grails-app/domain")
        deleteDir("grails-app/conf/hibernate")
        deleteDir("grails-app/conf/spring")
        deleteDir("grails-app/realms")
        deleteDir("grails-app/utils")
        deleteDir("grails-app/taglib")
        deleteDir("grails-app/views/layouts")
        deleteDir("grails-app/views")
        deleteDir("lib")
        deleteDir("scripts")
        deleteDir("src/groovy")
        deleteDir("src/java")
        deleteDir("src")
        deleteDir("web-app/css")
        deleteDir("web-app/images")
        deleteDir("web-app/js")
    }
}

eventTestPhasesStart = {
    //since we are in test mode, let's make sure that the quartz scheduler does not run
    //setting this property will ensure the scheduler does not start
    System.setProperty("metridoc.job.cliOnly", "true")
}