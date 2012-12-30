package metridoc.test

import metridoc.core.MetridocJob
import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.apache.camel.builder.RouteBuilder
import org.apache.commons.lang.ObjectUtils

class BarJob extends MetridocJob {

    def someProperty = "somePropertyValue"
    static triggers = MetridocJob.MIDNIGHT
    def dataSource

    @Override
    def doExecute() {
        profile("running bar") {
            assert grailsApplication
            Thread.sleep(1000)
        }

        log.info "targets ran $targetsRan"
        log.info "this identifying hash is ${System.identityHashCode(this)}"
        log.info "camelContext identifying hash is ${System.identityHashCode(getCamelJobContext())}"
        assert camelJobContext: "camel context not loaded"
        assert camelJobContext.registry.lookup("someProperty"): "property not found"
        assert !camelJobContext.registry.lookup("blah")
        assert camelJobContext.registry.lookup("quartzScheduler")
        assert producerJobTemplate

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

        log.info "target map is ${targetMap}"
    }
}
