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
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.fusesource.jansi.Ansi.ansi;

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 8/22/12
 * Time: 2:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class DefaultGrailsWorkflowClass extends AbstractInjectableGrailsClass implements GrailsWorkflowClass {

    public static final String WORKFLOW = "Workflow";
    public static final String RUN = "run";
    private Date previousEndTime = null;
    private Date previousFireTime = null;
    private String previousDuration = null;
    private AtomicBoolean running = new AtomicBoolean(false);
    private Throwable lastException = null;
    public static final Logger logger = LoggerFactory.getLogger(DefaultGrailsWorkflowClass.class);

    public DefaultGrailsWorkflowClass(Class clazz) {
        super(clazz, WORKFLOW);
    }

    public Object run() {

        boolean concurrentRun = false;
        if(running.get()) {
            String message = "Multiple instances of " + getName() + " are running, time stats for this run will not be collected";
            logger.warn(message);
        } else {
            running.getAndSet(true);
            previousFireTime = new Date();
        }

        Binding binding = null;
        try {
            logger.info("Building job " + getName());
            Script reference = (Script) getReferenceInstance();
            ScriptWrapper wrapper = (ScriptWrapper) reference.getBinding().getVariable("wrapper");
            reference.setBinding(new Binding());
            reference.getBinding().setVariable("grailsApp", getGrailsApplication());
            reference.getBinding().setVariable("config", getGrailsApplication().getConfig());
            reference.getBinding().setVariable("appCtx", getGrailsApplication().getMainContext());
            reference.getBinding().setVariable("wrapper", wrapper);
            JobBuilder.isJob(reference);

            Logger logger = LoggerFactory.getLogger("metridoc.job." + getName());
            reference.getBinding().setVariable("log", logger);

            if(!reference.getBinding().hasVariable("grailsConsole")) {
                GrailsConsoleFacade grailsConsole = new GrailsConsoleFacade();
                grailsConsole.setLogger(logger);
                reference.getBinding().setVariable("grailsConsole", grailsConsole);
            }

            binding = reference.getBinding();
            logger.info ("running the job " + getName());
            getMetaClass().invokeMethod(reference, RUN, new Object[]{});
            String targetName = "run" + getName();
            boolean hasTarget = binding.hasVariable(targetName);
            if(hasTarget) {
                logger.info ("running the target " + targetName + " for job " + getName());
                Closure closure = (Closure) binding.getVariable(targetName);
                return closure.call();
            } else {
                logger.warn(getName() + " workflow does not have a run" + getName() + " target");
                return null;
            }
        } catch (Exception e) {
            logger.error("Exception occurred while trying to run " + getName(), e);
            lastException = e;
            throw new RuntimeException(e);
        } finally {
            if (!concurrentRun) {
                previousEndTime = new Date();
                if (previousEndTime != null && previousFireTime != null) {
                    previousDuration = getPreviousDuration(previousEndTime.getTime() - previousFireTime.getTime());
                }
                running.getAndSet(false);
            }
            if(binding.hasVariable("camelScriptingContext")) {
                CamelContext camelContext = (CamelContext) binding.getVariable("camelScriptingContext");
                try {
                    camelContext.stop();
                } catch (Exception e) {
                    logger.warn("had troubles stopping camelContext {}", camelContext.toString());
                }
            }

            logger.info("finished running the job " + getName());
        }
    }

    public Date getPreviousEndTime() {
        return previousEndTime;
    }

    public boolean isRunning() {
        return running.get();
    }

    public Throwable getLastException() {
        return lastException;
    }

    public Date getPreviousFireTime() {
        return previousFireTime;
    }

    private static double doCalculation(long milliseconds, int denominator) {
        return Math.round(milliseconds / (double) denominator * 100) / (double) 100;
    }

    private static String getPreviousDuration(Long milliseconds) {
        if(milliseconds == null) {
            return null;
        }

        String currentFormat = String.valueOf(milliseconds) + "ms";
        int oneSecond = 1000;

        double calculation;
        if(milliseconds >= oneSecond) {
            calculation = doCalculation(milliseconds, oneSecond);
            currentFormat = String.valueOf(calculation) + "s";
        }

        int oneMinute = oneSecond * 60;
        if(milliseconds >= oneMinute) {
            calculation = doCalculation(milliseconds, oneMinute);
            currentFormat = String.valueOf(calculation) + "m";
        }

        int oneHour = oneMinute * 60;
        if(milliseconds >= oneHour) {
            calculation = doCalculation(milliseconds, oneHour);
            currentFormat = String.valueOf(calculation) + "h";
        }

        int oneDay = oneHour * 24;
        if(milliseconds >= oneDay) {
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
        boolean noWrapper = !binding.hasVariable("wrapper");

        if(noWrapper) {
            ScriptWrapper wrapper = new ScriptWrapper();
            wrapper.setWrappedScript(script);
            binding.setVariable("wrapper", wrapper);
        }

        return script;
    }
}