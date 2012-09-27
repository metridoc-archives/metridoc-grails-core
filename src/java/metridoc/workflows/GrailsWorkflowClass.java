package metridoc.workflows;

import org.codehaus.groovy.grails.commons.InjectableGrailsClass;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 8/22/12
 * Time: 1:50 PM
 * To change this template use File | Settings | File Templates.
 */
public interface GrailsWorkflowClass extends InjectableGrailsClass {
    Object run();
    Date getPreviousEndTime();
    boolean isRunning();
    Throwable getLastException();
    void stop();
}
