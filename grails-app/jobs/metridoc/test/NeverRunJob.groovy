package metridoc.test

import metridoc.core.MetridocJob



class NeverRunJob extends MetridocJob{

    static triggers = MetridocJob.MANUAL_RUN_TRIGGER

    def doExecute() {
        log.info "I should never run unless triggered manually!"
    }
}
