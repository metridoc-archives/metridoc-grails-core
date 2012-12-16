package metridoc.test

import metridoc.core.MetridocJob



class NeverRunJob extends MetridocJob{

    static triggers = MANUAL_RUN_TRIGGER

    def doExecute() {
        log.info "I should never run!"
    }
}
