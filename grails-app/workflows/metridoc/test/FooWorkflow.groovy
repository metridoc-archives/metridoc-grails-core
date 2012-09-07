package metridoc.test

class FooWorkflow extends Script {

    def homeService

    @Override
    Object run() {
        target(runFoo: "main target to run") {

            grailsConsole.info "hey from foo"

            //test DI
            assert homeService


            profile("profiling foo") {
                Thread.sleep(500)
            }
        }
    }
}





