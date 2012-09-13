package metridoc.test

class FooWorkflow extends Script {

    def barWorkflow

    @Override
    Object run() {
        target(runFoo: "the main target to run") {


            grailsConsole.info "running sample workflow"

            //test DI
            assert barWorkflow


            profile("profiling foo") {
                Thread.sleep(500)
            }
        }
    }
}





