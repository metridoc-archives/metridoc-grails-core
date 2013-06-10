package metridoc.test

import metridoc.core.MetridocJob

/**
 * a test job to illustrate and make sure many of the features of MetridocJob are actually working
 */
class BarJobService extends MetridocJob {

    def someProperty = "somePropertyValue"
    def dataSource
    def grailsApplication

    @Override
    def configure() {
        profile("running bar") {
            assert grailsApplication
            Thread.sleep(1000) //run one sec to check profiling
        }

        def camelRouteWorked = false

        asyncSend("seda:start", "hello")
        consume("seda:start") {
            camelRouteWorked = true
        }

        assert camelRouteWorked

        target(default: "the default target for job bar") {

            profile("profiling the default target for bar") {
                depends("barDependency")
                log.info "running the default target for bar"
            }
        }

        target(barDependency: "a target dependency that bar will run") {
            log.info "dependency ran"
        }

        target(directRun: "not called by default, but could be called via url with 'target' set") {
            log.info "directRun target called in BarJobService"
        }

    }
}
