package metridoc.workflows

import org.codehaus.groovy.grails.commons.ArtefactHandlerAdapter
import java.lang.reflect.Method
import org.springframework.util.ReflectionUtils

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 8/22/12
 * Time: 1:47 PM
 * To change this template use File | Settings | File Templates.
 */
class WorkflowArtefactHandler extends ArtefactHandlerAdapter {

    public static final String TYPE = "Workflow"

    WorkflowArtefactHandler() {
        super(TYPE, GrailsWorkflowClass.class, DefaultGrailsWorkflowClass.class, null)
    }

    @Override
    boolean isArtefactClass(Class clazz) {
        def isScript = Script.class.isAssignableFrom(clazz)
        def runMethod = ReflectionUtils.findMethod(clazz, DefaultGrailsWorkflowClass.RUN);
        def hasRunMethod = runMethod != null

        return isScript || hasRunMethod
    }
}
