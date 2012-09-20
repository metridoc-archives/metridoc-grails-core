package metridoc.test

import org.apache.camel.ProducerTemplate
import org.apache.commons.lang.ObjectUtils

class FooWorkflow extends Script {

    def barWorkflow

    @Override
    Object run() {
        target(runFoo: "the main target to run") {


            grailsConsole.info "running sample workflow foo"

            //test DI
            assert barWorkflow


            profile("profiling foo") {
                Thread.sleep(500)
            }

            def routeRan = false
            runRoute {
                from("direct:fooWorkflow").process {
                    routeRan = true
                }
            }

            camelScriptingTemplate.requestBody("direct:fooWorkflow", ObjectUtils.NULL)
            assert routeRan
        }
    }
}





