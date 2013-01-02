package metridoc.test

import metridoc.core.MetridocJob

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 11/14/12
 * Time: 8:09 PM
 * To change this template use File | Settings | File Templates.
 */
class ErrorJob extends MetridocJob {

    static triggers = MetridocJob.MIDNIGHT

    def doExecute() {
        log.error "about to throw an error from ErrorJob"
        throw new RuntimeException("I meant to do that")
    }
}
