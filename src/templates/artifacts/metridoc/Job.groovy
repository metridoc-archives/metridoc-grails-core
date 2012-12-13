@artifact.package@

import metridoc.core.MetridocJob

class @artifact.name@ extends MetridocJob{
    static triggers = {
        simple repeatInterval: 5000l // execute job once in 5 seconds
    }

    def doExecute() {
        // execute job
    }
}
