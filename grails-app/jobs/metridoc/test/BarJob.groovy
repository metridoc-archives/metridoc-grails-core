package metridoc.test

import metridoc.core.MetridocJob



class BarJob extends MetridocJob{
    static triggers = {
      simple repeatInterval: 5000l // execute job once in 5 seconds
    }

    @Override
    def doExecute() {
        profile("running bar") {
            Thread.sleep(1000)
        }
    }
}
