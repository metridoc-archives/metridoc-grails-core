package metridoc.workflows;

import groovy.lang.Script;
import jline.*;
import metridoc.dsl.JobBuilder;
import org.apache.tools.ant.BuildException;
import org.codehaus.groovy.grails.cli.ScriptExitException;
import org.codehaus.groovy.grails.cli.interactive.CandidateListCompletionHandler;
import org.codehaus.groovy.grails.cli.logging.GrailsConsoleErrorPrintStream;
import org.codehaus.groovy.grails.cli.logging.GrailsConsolePrintStream;
import org.codehaus.groovy.grails.commons.AbstractInjectableGrailsClass;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.codehaus.groovy.runtime.StackTraceUtils;
import org.codehaus.groovy.runtime.typehandling.NumberMath;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.*;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.Stack;

import static org.fusesource.jansi.Ansi.Color.DEFAULT;
import static org.fusesource.jansi.Ansi.Color.RED;
import static org.fusesource.jansi.Ansi.Color.YELLOW;
import static org.fusesource.jansi.Ansi.Erase.FORWARD;
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
    private boolean running = false;
    private Throwable lastException = null;

    public DefaultGrailsWorkflowClass(Class clazz) {
        super(clazz, WORKFLOW);
    }

    @Override
    public synchronized Object run() {
        running = true;
        Script reference = (Script) getReferenceInstance();

        Logger logger = LoggerFactory.getLogger("metridoc.job." + getName());
        GrailsConsoleFacade grailsConsole = new GrailsConsoleFacade();
        grailsConsole.setLogger(logger);
        reference.getBinding().setVariable("grailsConsole", grailsConsole);
        JobBuilder.isJob(reference);

        try {
            return getMetaClass().invokeMethod(reference, RUN, new Object[]{});
        } catch (Exception e) {
            logger.error("Exception occurred while trying to run " + getName(), e);
            lastException = e;
            throw new RuntimeException(e);
        } finally {
            previousEndTime = new Date();
            running = false;
        }
    }

    @Override
    public Date getPreviousEndTime() {
        return previousEndTime;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public Throwable getLastException() {
        return lastException;
    }
}
