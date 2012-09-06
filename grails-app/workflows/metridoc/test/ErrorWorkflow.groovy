package metridoc.test

target(runError: "main target to run") {
    throw new RuntimeException("oops line 1\noops line 2")
}


