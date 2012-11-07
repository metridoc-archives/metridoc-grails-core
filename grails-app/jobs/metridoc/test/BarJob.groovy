package metridoc.test

import metridoc.core.MetridocJob



class BarJob extends MetridocJob{
    static triggers = {
      simple repeatInterval: 60000l // execute job once every minute
    }

    @Override
    def doExecute() {
        profile("running bar") {
            Thread.sleep(1000)
        }
    }
}
