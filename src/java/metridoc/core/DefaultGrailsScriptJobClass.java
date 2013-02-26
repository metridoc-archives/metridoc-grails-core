package metridoc.core;

import grails.plugin.quartz2.DefaultGrailsJobClass;
import groovy.lang.Binding;
import groovy.lang.Script;
import org.quartz.JobExecutionContext;

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 2/25/13
 * Time: 4:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class DefaultGrailsScriptJobClass extends DefaultGrailsJobClass {
    public static final String RUN = "run";

    public DefaultGrailsScriptJobClass(Class clazz) {
        super(clazz);
    }

    @Override
    public void execute() {
        if(isScript()) {
            executeScript();
            return;
        }

        super.execute();
    }

    @Override
    public void execute(JobExecutionContext context) {
        if(isScript()) {
            executeScript(context);
            return;
        }
        super.execute(context);
    }

    private boolean isScript() {
        return Script.class.isAssignableFrom(getClazz());
    }

    protected void executeScript() {
        getMetaClass().invokeMethod(getReferenceInstance(), RUN, new Object[]{});
    }

    protected void executeScript(JobExecutionContext context) {
        Binding binding = (Binding) getMetaClass()
                .invokeMethod(getReferenceInstance(), "getBinding", new Object[]{});
        binding.setVariable("jobExecutionContext", context);
        getMetaClass().invokeMethod(getReferenceInstance(), RUN, new Object[]{});
    }
}
