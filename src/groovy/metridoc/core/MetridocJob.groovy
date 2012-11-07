package metridoc.core

import org.apache.camel.builder.RouteBuilder
import org.quartz.JobExecutionContext
import org.apache.commons.lang.SystemUtils
import org.slf4j.LoggerFactory



abstract class MetridocJob {

    def concurrent = false
    def applicationContext
    static final log = LoggerFactory.getLogger(MetridocJob)

    def execute(JobExecutionContext context) {
        doExecute(context)
        doExecute()
    }

    def runRoute(Closure closure) {

    }

    def runRoute(RouteBuilder builder) {

    }

    def target(Map data, Closure closure) {

    }

    def dependsOn(String targetName) {

    }

    /**
     * profiles a chunk of code stating when it starts and finishes
     * @param description description of the chunk of code
     * @param closure the code to run
     */
    def profile(String description, Closure closure) {
        def start = System.currentTimeMillis()
        log.info "profiling [$description] start"
        closure.call()
        def end = System.currentTimeMillis()
        log.info "profiling [$description] finished ${end - start} ms"
    }

    def includeTargets(Script script) {

    }

    def doExecute() {
        //override this
    }

    def doExecute(JobExecutionContext context) {
        //override this if job details are required
    }
}
