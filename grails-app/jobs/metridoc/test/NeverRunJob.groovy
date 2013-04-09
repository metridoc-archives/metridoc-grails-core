package metridoc.test

import metridoc.core.MetridocJob



class NeverRunJob extends MetridocJob {

    def doExecute() {
        log.info "I should never run unless triggered manually!"
    }
}
