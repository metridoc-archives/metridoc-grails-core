package metridoc.test

import static org.slf4j.LoggerFactory.getLogger

def log = getLogger("foo.logger")
log.info "I am a script job"

def foo = config.foo
if (foo) {
    log.info "the variable foo exists from the config, printing now"
    log.info foo
} else {
    log.info "Could not find the variable foo in the config"
}