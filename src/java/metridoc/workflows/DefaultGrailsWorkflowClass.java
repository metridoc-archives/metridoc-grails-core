package metridoc.workflows;

import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.Script;
import metridoc.dsl.JobBuilder;
import org.codehaus.groovy.grails.commons.AbstractInjectableGrailsClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
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

        if(running.get()) {
            String message = "Job " + getName() + " is already running";
            logger.info(message);
            return null;
        }
        running.getAndSet(true);
        previousFireTime = new Date();
        Script reference = (Script) getReferenceInstance();
        ScriptWrapper wrapper = (ScriptWrapper) reference.getBinding().getVariable("wrapper");
        reference.setBinding(new Binding());
        reference.getBinding().setVariable("wrapper", wrapper);
        JobBuilder.isJob(reference);

        Logger logger = LoggerFactory.getLogger("metridoc.job." + getName());

        if(!reference.getBinding().hasVariable("grailsConsole")) {
            GrailsConsoleFacade grailsConsole = new GrailsConsoleFacade();
            grailsConsole.setLogger(logger);
            reference.getBinding().setVariable("grailsConsole", grailsConsole);
        }

        try {
            getMetaClass().invokeMethod(reference, RUN, new Object[]{});
            Binding binding = reference.getBinding();
            String targetName = "run" + getName();
            boolean hasTarget = binding.hasVariable(targetName);
            if(hasTarget) {
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
            previousEndTime = new Date();
            previousDuration = getPreviousDuration(previousEndTime.getTime() - previousFireTime.getTime());
            running.getAndSet(false);
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
        Script script = (Script) super.getReferenceInstance();
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