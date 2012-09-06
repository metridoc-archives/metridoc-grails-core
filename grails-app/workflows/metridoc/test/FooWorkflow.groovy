package metridoc.test

target(runFoo: "main target to run") {
    grailsConsole.info "hey from foo"

    assert appCtx.containsBean("homeService")

    profile("profiling foo") {
        Thread.sleep(500)
    }
}



