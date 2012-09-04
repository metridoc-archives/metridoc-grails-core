package metridoc.test

target(runFoo: "main target to run") {
    grailsConsole.info "hey from foo"

    profile("profiling foo") {
        Thread.sleep(500)
    }
}



