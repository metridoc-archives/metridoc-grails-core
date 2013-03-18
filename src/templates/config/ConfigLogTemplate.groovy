//TEMPLATE_LOG_4J
log4j = {

    appenders {
        rollingFile name: "file",
                maxBackupIndex: 10,
                maxFileSize: "1MB",
                file: "${config.metridoc.home}/logs/metridoc.log"

        rollingFile name: "stacktrace",
                maxFileSize: "1MB",
                maxBackupIndex: 10,
                file: "${config.metridoc.home}/logs/metridoc-stacktrace.log"

        //not used yet... this will be where we log cli jobs
        rollingFile name: "jobLog",
                maxFileSize: "1MB",
                maxBackupIndex: 10,
                file: "${config.metridoc.home}/logs/metridoc-job.log"
    }

    warn 'metridoc.camel',
            'metridoc.utils.CamelUtils'

    info 'metridoc'

    //since it it running via commandline, it is assumed that standard out is only needed
    if ("true" == System.getProperty("metridoc.job.cliOnly")) {
        root {
            error 'stdout'
        }
    } else {
        if ("false" == System.getProperty("metridoc.job.loggedLogLocation", "false")) {
            println "INFO: logs will be stored at ${config.metridoc.home}/logs"
            //avoids duplicate logging
            System.setProperty("metridoc.job.loggedLogLocation", "true")
        }
        root {
            error 'stdout', 'file'
        }
    }
}
//TEMPLATE_LOG_4J