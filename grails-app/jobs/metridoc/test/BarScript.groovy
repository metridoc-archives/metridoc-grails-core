package metridoc.test

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 11/7/12
 * Time: 3:36 PM
 * To change this template use File | Settings | File Templates.
 */
class BarScript extends Script {

    @Override
    Object run() {
        target(runBar: "running bar") {
            profile("doing some basic tests for profiling in external file") {
                log.info "running a target from an external script"
            }
        }
    }
}
