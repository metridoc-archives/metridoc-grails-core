package metridoc.core

import org.quartz.InterruptableJob
import org.quartz.JobExecutionContext
import org.quartz.JobExecutionException
import org.quartz.UnableToInterruptJobException

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 2/26/13
 * Time: 12:21 PM
 * To change this template use File | Settings | File Templates.
 */
class ScriptJob implements InterruptableJob {
    private Script script
    String arguments

    ScriptJob() {
    }

    ScriptJob(Script script) {
        this.script = script
    }

    void interrupt() throws UnableToInterruptJobException {
        script.binding.setVariable("interrupt", true)
    }

    void execute(JobExecutionContext context) throws JobExecutionException {
        Binding binding = script.binding
        binding.setVariable("jobExecutionContext", context)
        def config = context.getTrigger().getJobDataMap().get("config")

        if (config != null && config instanceof Map) {
            QuartzService.addConfigToBinding(config, binding)
        }

        if (arguments) {
            script.args = arguments.split(" ")
        }
        def originalClassLoader = Thread.currentThread().contextClassLoader
        try {
            Thread.currentThread().setContextClassLoader(script.class.classLoader)
            script.run()
        } finally {
            Thread.currentThread().setContextClassLoader(originalClassLoader)
        }
    }
}
