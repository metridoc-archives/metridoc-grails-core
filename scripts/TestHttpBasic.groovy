includeTargets << grailsScript("_GrailsInit")

target(main: "The description of the script goes here!") {
    grailsConsole.info args
}

setDefaultTarget(main)
