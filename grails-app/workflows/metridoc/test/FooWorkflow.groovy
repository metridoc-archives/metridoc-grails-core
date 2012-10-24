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


            //make things run long enough so we can test stopping
            profile("profiling foo") {
                (0..2500000).each{
                    long count
                    (1L..Long.MAX_VALUE).each {
                        count = it
                    }
                    if(it % 5000) {
                        if(Thread.currentThread().isInterrupted()) {
                            throw new RuntimeException("prematurely stopped")
                        }
                    }
                }
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





