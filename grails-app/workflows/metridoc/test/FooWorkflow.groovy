package metridoc.test

grailsConsole.info "hey"

profile("profiling foo") {
    Thread.sleep(60000)
}
