package metridoc.workflows;

import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.Script;
import metridoc.dsl.JobBuilder;
import org.apache.camel.CamelContext;
import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.grails.commons.AbstractInjectableGrailsClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class DefaultGrailsWorkflowClass extends AbstractInjectableGrailsClass implements GrailsWorkflowClass {

    public static final String WORKFLOW = "Workflow";
    public static final String RUN = "run";
    private Date previousEndTime = null;
    private Date previousFireTime = null;
    private String previousDuration = null;
    private AtomicReference<Thread> runningThread = new AtomicReference<Thread>();
    private Throwable lastException = null;
    public static final Logger logger = LoggerFactory.getLogger(DefaultGrailsWorkflowClass.class);

    public DefaultGrailsWorkflowClass(Class clazz) {
        super(clazz, WORKFLOW);
    }

    public Object run() {

        Thread jobToJoin = null;
        final Object[] result = {null};
        synchronized (this) {
            if (isRunning()) {
                String message = getName() + " is already running, joining job already in progress";
                logger.warn(message);
                jobToJoin = runningThread.get();
            } else {

                Runnable runnable = new Runnable() {
                    public void run() {
                        //gets around the 'final' unmutable issues
                        result[0] = doRun();
                    }
                };

                Thread jobThread = new Thread(runnable);
                runningThread.getAndSet(jobThread);
                runningThread.get().start();
            }
        }

        try {
            if (jobToJoin != null) {
                jobToJoin.join();
                return null;
            } else {
                runningThread.get().join();
                return result[0];
            }
        } catch (InterruptedException e) {
            logger.error("job " + getName() + "interrupted (likely stopped)", e);
            return null;
        } finally {
            runningThread.getAndSet(null);
        }
    }

    private Object doRun() {
        previousFireTime = new Date();
        Binding binding = null;
        Map<String, Object> variables = null;
        boolean noError = true;
        try {
            logger.info("Building job " + getName());
            Script reference = (Script) getReferenceInstance();
            ScriptWrapper wrapper = (ScriptWrapper) reference.getBinding().getVariable("wrapper");
            reference.setBinding(new Binding());
            binding = reference.getBinding();
            binding.setVariable("grailsApp", getGrailsApplication());
            binding.setVariable("config", getGrailsApplication().getConfig());
            binding.setVariable("appCtx", getGrailsApplication().getMainContext());
            binding.setVariable("wrapper", wrapper);
            JobBuilder.isJob(reference);

            Logger logger = LoggerFactory.getLogger("metridoc.job." + getName());
            binding.setVariable("log", logger);

            variables = binding.getVariables();
            if (!variables.containsKey("grailsConsole")) {
                GrailsConsoleFacade grailsConsole = new GrailsConsoleFacade();
                grailsConsole.setLogger(logger);
                reference.getBinding().setVariable("grailsConsole", grailsConsole);
            }

            logger.info("running the job " + getName());
            getMetaClass().invokeMethod(reference, RUN, new Object[]{});
            String targetName = "run" + getName();
            boolean hasTarget = variables.containsKey(targetName);
            if (hasTarget) {
                logger.info("running the target " + targetName + " for job " + getName());
                Closure closure = (Closure) binding.getVariable(targetName);
                return closure.call();
            } else {
                logger.warn(getName() + " workflow does not have a run" + getName() + " target");
                return null;
            }
        } catch (Throwable e) {
            logger.error("Exception occurred while trying to run " + getName(), e);
            lastException = e;
            noError = false;
            throw new RuntimeException(e);
        } finally {
            previousEndTime = new Date();
            if (previousEndTime != null && previousFireTime != null) {
                previousDuration = getPreviousDuration(previousEndTime.getTime() - previousFireTime.getTime());
            }
            if (binding != null) {
                if (variables.containsKey("camelScriptingContext")) {
                    CamelContext camelContext = (CamelContext) binding.getVariable("camelScriptingContext");
                    try {
                        camelContext.stop();
                    } catch (Exception e) {
                        logger.warn("had troubles stopping camelContext {}", camelContext.toString());
                    }
                }
            }

            logger.info("finished running the job " + getName());
            if (noError) {
                lastException = null;
            }
        }
    }

    public Date getPreviousEndTime() {
        return previousEndTime;
    }

    public synchronized boolean isRunning() {
        return runningThread.get() != null;
    }

    public Throwable getLastException() {
        return lastException;
    }

    public void stop() {
        Thread runningThread = this.runningThread.get();
        if (runningThread != null) {
            runningThread.interrupt();
            logger.info("Trying to stop " + getName());
        } else {
            logger.warn("Could not stop " + getName() + " since it is not running");
        }
    }

    public Date getPreviousFireTime() {
        return previousFireTime;
    }

    private static double doCalculation(long milliseconds, int denominator) {
        return Math.round(milliseconds / (double) denominator * 100) / (double) 100;
    }

    private static String getPreviousDuration(Long milliseconds) {
        if (milliseconds == null) {
            return null;
        }

        String currentFormat = String.valueOf(milliseconds) + "ms";
        int oneSecond = 1000;

        double calculation;
        if (milliseconds >= oneSecond) {
            calculation = doCalculation(milliseconds, oneSecond);
            currentFormat = String.valueOf(calculation) + "s";
        }

        int oneMinute = oneSecond * 60;
        if (milliseconds >= oneMinute) {
            calculation = doCalculation(milliseconds, oneMinute);
            currentFormat = String.valueOf(calculation) + "m";
        }

        int oneHour = oneMinute * 60;
        if (milliseconds >= oneHour) {
            calculation = doCalculation(milliseconds, oneHour);
            currentFormat = String.valueOf(calculation) + "h";
        }

        int oneDay = oneHour * 24;
        if (milliseconds >= oneDay) {
            calculation = doCalculation(milliseconds, oneDay);
            currentFormat = String.valueOf(calculation) + "d";
        }

        return currentFormat;
    }

    public String getPreviousDuration() {
        return previousDuration;
    }

    @Override
    public Object getReferenceInstance() {
        String shortName = StringUtils.uncapitalize(getShortName());
        Script script = (Script) getGrailsApplication().getMainContext().getBean(shortName);
        Binding binding = script.getBinding();
        Map<String, Object> variables = binding.getVariables();
        boolean noWrapper = !variables.containsKey("wrapper");

        if (noWrapper) {
            ScriptWrapper wrapper = new ScriptWrapper();
            wrapper.setWrappedScript(script);
            binding.setVariable("wrapper", wrapper);
        }

        return script;
    }
}