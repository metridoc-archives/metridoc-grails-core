package metridoc.workflows;

import org.codehaus.groovy.grails.commons.AbstractInjectableGrailsClass;

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

    public DefaultGrailsWorkflowClass(Class clazz) {
        super(clazz, WORKFLOW);
    }

    @Override
    public Object run() {
        return getMetaClass().invokeMethod(getReferenceInstance(), RUN, new Object[]{});
    }
}
