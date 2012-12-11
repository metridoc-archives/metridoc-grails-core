eventPackagingEnd = { msg ->

    Properties properties = new Properties()
    properties.load(new File("application.properties").newInputStream())

    def jobOnly = properties.hasProperty("metridoc.jobOnly") ? Boolean.valueOf(properties.getProperty("metridoc.jobOnly")) : false

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
    }
}