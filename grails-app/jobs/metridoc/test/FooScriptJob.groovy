package metridoc.test

println "I am a script job"

if (binding.hasVariable("foo")) {
    println "the variable foo exists from the config, printing now"
    println foo
} else {
    println "Could not find the variable foo in the config"
}