package metridoc.core

import metridoc.camel.CamelScript
import metridoc.camel.CamelScriptRegistry
import metridoc.camel.GroovyRouteBuilder
import metridoc.camel.SqlPlusComponent
import metridoc.core.tools.CamelTool
import metridoc.core.tools.ParseArgsTool
import metridoc.core.tools.RunnableTool
import org.apache.camel.Exchange
import org.apache.camel.builder.RouteBuilder
import org.apache.commons.lang.StringUtils
import org.slf4j.LoggerFactory

import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

/**
 * This is the primary class used for running MetriDoc jobs.  Although unnecessary to actually create a periodic job,
 * it contains helpful methods for routing and profiling tasks, in addition to common triggers.
 */
abstract class MetridocJob extends RunnableTool {

    /**
     * This was used when we couldn't manually set triggers in the web application.  This is no longer needed and
     * might be removed in newer versions of the core.
     * Unfortunately for quartz to actually register a job, it must have some fire time in the future.  This trigger
     * has an arbitrarily long start up delay (50 years).  By doing this the job can be managed via the quartzscheduler.
     *
     * @deprecated
     */
    static final MANUAL_RUN_TRIGGER = {
        def fiftyYears = TimeUnit.DAYS.toMillis(365 * 50)
        simple repeatInterval: 1000, startDelay: fiftyYears
    }

    /**
     * represents a midnight trigger.  Here for convenience when scheduling jobs in code
     */
    static final MIDNIGHT = {
        cron cronExpression: "0 0 0 * * ?"
    }

    private static final jobLogger = LoggerFactory.getLogger(MetridocJob)
    /**
     * all metridoc jobs are not concurrent by default.  Any needed concurrency should be incorporated into the job
     * itself.  This basically provides a hint to quartz
     */
    public final boolean concurrent = false

    /**
     * runs the closure as a route within the {@link GroovyRouteBuilder}.  Really handy for database migrations and
     * batch processing
     *
     * @param closure
     * @return
     */
    def runRoute(Closure closure) {
        CamelScript.components.put("sqlplus", SqlPlusComponent)
        closure.delegate = this
        CamelScript.runRoute(closure)
    }

    /**
     * instead of using runRoute with a closure, you can pass a RouteBuilder instead.  This can be convenient if someone
     * wants to use their own {@link RouteBuilder} or wants to benefit from code completion
     *
     * @param builder
     */
    def runRoute(RouteBuilder builder) {
        CamelScript.components.put("sqlplus", SqlPlusComponent)
        def mockClosure = {}
        mockClosure.delegate = builder
        def registry = new CamelScriptRegistry(closure: mockClosure)
        CamelScript.runRouteBuilders(registry, builder)
    }

    void consume(String endpoint, Closure closure) {
        includeTool(CamelTool).consume(endpoint, closure)
    }

    void consumeNoWait(String endpoint, Closure closure) {
        includeTool(CamelTool).consumeNoWait(endpoint, closure)
    }

    void consumeWait(String endpoint, long wait, Closure closure) {
        includeTool(CamelTool).consumeWait(endpoint, wait, closure)
    }

    void consumeTillDone(String endpoint, long wait, Closure closure) {
        includeTool(CamelTool).consumeTillDone(endpoint, wait, closure)
    }

    Exchange send(String endpoint, body) {
        includeTool(CamelTool).send(endpoint, body)
    }

    Exchange send(String endpoint, body, Map headers) {
        includeTool(CamelTool).send(endpoint, body, headers)
    }

    Future<Exchange> asyncSend(String endpoint, body) {
        includeTool(CamelTool).asyncSend(endpoint, body)
    }

    Future<Exchange> asyncSend(String endpoint, body, Map headers) {
        includeTool(CamelTool).asyncSend(endpoint, body, headers)
    }

    @Override
    def execute() {
        def parseTool = includeTool(ParseArgsTool)
        //in case args was set after this was initialized
        parseTool.setBinding(binding)

        def thisToolName = StringUtils.uncapitalize(this.getClass().simpleName)
        if (!binding.hasVariable(thisToolName)) {
            binding.setVariable(thisToolName, this)
        }
        //redo injection in case properties were set after including the tool
        def manager = MetridocScript.getManager(binding)
        manager.handlePropertyInjection(this)
        configure()
        doExecute()
        String target = getVariable("target", String)
        if (target) {
            setDefaultTarget(target)
        }
        String defaultTarget = manager.defaultTarget
        if (manager.targetMap.containsKey(defaultTarget)) {
            manager.runDefaultTarget()
        }
    }

    @Override
    def configure() {
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }

    def doExecute() {}

    void interrupt() {
        MetridocScript.getManager(binding).interrupt()
    }
}
