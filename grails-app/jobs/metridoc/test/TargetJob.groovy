package metridoc.test

import metridoc.core.MetridocJob

/**
 * Created with IntelliJ IDEA on 4/9/13
 * @author Tommy Barker
 */
class TargetJob extends MetridocJob {


    @Override
    def configure() {
        target(foo: "run foo") {
            log.info "ran foo"
        }

        target(bar: "run bar") {
            log.info "ran foo"
        }
    }
}
