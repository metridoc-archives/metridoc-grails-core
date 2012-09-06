package metridoc.workflows;

import groovy.lang.Script;
import org.codehaus.groovy.grails.commons.ArtefactHandlerAdapter;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 8/22/12
 * Time: 1:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class WorkflowArtefactHandler extends ArtefactHandlerAdapter {

    public static final String TYPE = "Workflow";

    public WorkflowArtefactHandler() {
        super(TYPE, GrailsWorkflowClass.class, DefaultGrailsWorkflowClass.class, null, true);
    }

    @Override
    public boolean isArtefactClass(Class clazz) {
        boolean hasWorkflowInTheName = clazz.getName().endsWith(TYPE);

        if(!hasWorkflowInTheName) {
            return false;
        }

        boolean isScript = Script.class.isAssignableFrom(clazz);
        Method runMethod = ReflectionUtils.findMethod(clazz, DefaultGrailsWorkflowClass.RUN);
        boolean hasRunMethod = runMethod != null;

        return isScript && hasRunMethod;
    }
}
