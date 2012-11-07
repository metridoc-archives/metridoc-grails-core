package metridoc.core

import org.apache.camel.builder.RouteBuilder
import org.quartz.JobExecutionContext



abstract class MetridocJob {

    def concurrent = false
    def applicationContext

    def runRoute(Closure closure) {

    }

    def runRoute(RouteBuilder builder) {

    }

    def target(Map data, Closure closure) {

    }

    def dependsOn(String targetName) {

    }

    def profile(Closure closure) {

    }

    def includeTargets(Script script) {

    }

    def doExecute() {

    }

    def doExecute(JobExecutionContext context) {

    }
}
