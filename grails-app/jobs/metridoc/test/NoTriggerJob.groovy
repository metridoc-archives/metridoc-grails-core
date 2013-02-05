package metridoc.test

import metridoc.core.MetridocJob



class NoTriggerJob extends MetridocJob {

    @Override
    def doExecute() {
        log.info "I ran"
    }
}
