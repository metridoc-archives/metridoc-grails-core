package metridoc.core

import grails.util.Environment

class JobConfig {

    /**
     * name of the job
     */
    String triggerName
    /**
     * this can either be a url or a direct configuration
     */
    String config

    boolean configIsAUrl() {
        if (config == null) return false

        try {
            new URL(config)
            return true
        } catch (MalformedURLException e) {
            return false
        }
    }

    ConfigObject generateConfigObject() {
        if (config) {
            def environment = Environment.current.name
            def slurper = new ConfigSlurper(environment)
            if (configIsAUrl()) {
                return slurper.parse(new URL(config))
            }
            def parsedConfig = slurper.parse(config)
            return parsedConfig
        }

        return null
    }

    static constraints = {
        config(
                nullable: true,
                maxSize: Integer.MAX_VALUE,
                validator: {
                    if (it) {
                        try {
                            new URL(it)
                            return true
                        } catch (MalformedURLException e) {
                        }
                        def exception = getConfigException(it)
                        if (exception) {
                            return "invalid.config"
                        }
                        return true
                    }

                    return true
                }
        )
    }

    static Throwable getConfigException(String config) {
        try {
            new ConfigSlurper().parse(config)
            return null
        } catch (Throwable e) {
            e.stackTrace.each {

            }
            return e
        }
    }
}
