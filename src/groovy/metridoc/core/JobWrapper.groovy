package metridoc.core

import grails.plugin.quartz2.GrailsJobClassConstants
import org.quartz.InterruptableJob
import org.quartz.JobExecutionContext
import org.quartz.JobExecutionException
import org.quartz.UnableToInterruptJobException
import org.springframework.util.ReflectionUtils

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 4/9/13
 * Time: 8:44 AM
 * To change this template use File | Settings | File Templates.
 */
class JobWrapper implements InterruptableJob {

    public static final String INTERRUPT = "interrupt"
    def job

    @Override
    void interrupt() throws UnableToInterruptJobException {
        if (jobHasBinding()) {
            if (job.binding == null) {
                job.binding = new Binding()
            }
            Binding binding = job.binding
            binding.setVariable(INTERRUPT, true)
        }

        if (jobHasInterruptMethod()) {
            job.interrupt()
        }
    }

    @Override
    void execute(JobExecutionContext context) throws JobExecutionException {
        if (jobHasBinding()) {
            Binding binding = job.binding
            binding.setVariable("jobExecutionContext", context)
            def jobDataMap = context.getTrigger().getJobDataMap()
            def config = jobDataMap.get("config")

            String arguments
            if (jobDataMap.containsKey("args")) {
                arguments = jobDataMap["args"]
            }

            if (config != null && config instanceof Map) {
                QuartzService.addConfigToBinding(config, binding)
            }

            if (arguments) {
                def args = arguments.split("\\s+") as String[]
                binding.setVariable("args", args)
            }

            if (job instanceof Script) {
                def originalClassLoader = Thread.currentThread().contextClassLoader
                try {
                    Thread.currentThread().setContextClassLoader(job.getClass().classLoader)
                    job.run()
                } finally {
                    Thread.currentThread().setContextClassLoader(originalClassLoader)
                }
            } else {
                def executeMethod = ReflectionUtils.findMethod(job.getClass(), GrailsJobClassConstants.EXECUTE, (Class<?>[]) null);
                if (executeMethod == null) {
                    throw new IllegalArgumentException(job.getClass().getName() + " should declare #execute() method");
                }
                switch (executeMethod.getParameterTypes().length) {
                    case 0:
                        job.execute()
                        break;
                    case 1:
                        job.execute(jobExecutionContext)
                        break;
                    default:
                        throw new IllegalArgumentException(job.getClass().getName() + "#execute() method should take either no arguments or one argument of type JobExecutionContext");
                }
            }
        }
    }

    boolean jobHasBinding() {
        def properties = job.metaClass.properties
        return properties.find { it.name == "binding" && Binding.isAssignableFrom(it.type) }
    }

    boolean jobHasInterruptMethod() {
        def interruptMethod = ReflectionUtils.findMethod(job.getClass(), INTERRUPT, (Class<?>[]) null);
        return interruptMethod != null
    }
}
