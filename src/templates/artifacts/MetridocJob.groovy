@artifact.package@
class @artifact.name@ extends MetridocJob {
    static triggers = {
        simple repeatInterval: 60000l // execute job once in 60 seconds
    }

    def doExecute() {
        // execute job
    }
}
