package metridoc.test

import metridoc.core.MetridocJob
import org.apache.commons.lang.ObjectUtils
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.Processor
import org.apache.camel.Exchange



class BarJob extends MetridocJob{

    def someProperty = "somePropertyValue"
    static triggers = {
      simple name:  "basic trigger", repeatInterval: 60000l// execute job once every minute
    }

    @Override
    def doExecute() {
        profile("running bar") {
            assert grailsApplication
            Thread.sleep(1000)
        }

        assert camelJobContext: "camel context not loaded"
        assert camelJobContext.registry.lookup("someProperty"): "property not found"
        assert !camelJobContext.registry.lookup("blah")
        assert camelJobContext.registry.lookup("quartzScheduler")
        assert producerJobTemplate

        def camelRouteWorked = false
        runRoute {
            from("direct:startBarRoute").process {
                camelRouteWorked = true
            }
        }

        def camelRouteWithRouteBuilderWorked = false
        runRoute(
            new RouteBuilder() {

                @Override
                void configure() {
                    final processor = new Processor(){

                        @Override
                        void process(Exchange exchange) {
                            camelRouteWithRouteBuilderWorked = true
                        }
                    }
                    from("direct:startBarRouteRouteBuilder").process(processor)
                }
            }
        )

        producerJobTemplate.requestBody("direct:startBarRoute", ObjectUtils.NULL)
        producerJobTemplate.requestBody("direct:startBarRouteRouteBuilder", ObjectUtils.NULL)

        assert camelRouteWorked
        assert camelRouteWithRouteBuilderWorked

        target(default: "the default target for job bar") {
            profile("profiling the default target for bar") {
                log.info "runnng the default target for bar"
            }
        }
    }
}
