package metridoc.test

import metridoc.core.MetridocJob
import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.apache.camel.builder.RouteBuilder

class BarJob extends MetridocJob {

    def someProperty = "somePropertyValue"
    static triggers = MetridocJob.MIDNIGHT
    def dataSource
    def grailsApplication

    @Override
    def doExecute() {
        profile("running bar") {
            assert grailsApplication
            Thread.sleep(10000)
        }

        def camelRouteWorked = false
        runRoute {
            from("direct:start").process {
                camelRouteWorked = true
            }
        }

        def camelRouteWithRouteBuilderWorked = false
        runRoute(
                new RouteBuilder() {

                    @Override
                    void configure() {
                        final processor = new Processor() {

                            @Override
                            void process(Exchange exchange) {
                                camelRouteWithRouteBuilderWorked = true
                            }
                        }
                        from("direct:start").process(processor)
                    }
                }
        )

        assert camelRouteWorked
        assert camelRouteWithRouteBuilderWorked

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
            log.info "directRun target called in BarJob"
        }

    }
}
