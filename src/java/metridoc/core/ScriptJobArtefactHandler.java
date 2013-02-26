package metridoc.core;

import grails.plugin.quartz2.DefaultGrailsJobClass;
import grails.plugin.quartz2.GrailsJobClass;
import grails.plugin.quartz2.JobArtefactHandler;
import groovy.lang.Script;
import org.codehaus.groovy.grails.commons.ArtefactHandlerAdapter;

import static grails.plugin.quartz2.DefaultGrailsJobClass.*;

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 2/25/13
 * Time: 4:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class ScriptJobArtefactHandler extends ArtefactHandlerAdapter {

    JobArtefactHandler jobArtefactHandler = new JobArtefactHandler();

    public ScriptJobArtefactHandler() {
        super(DefaultGrailsJobClass.JOB, GrailsJobClass.class, DefaultGrailsScriptJobClass.class, null);
    }

    public boolean isArtefactClass(Class clazz) {
        if(jobArtefactHandler.isArtefactClass(clazz)) return true;

        boolean assignableFrom = Script.class.isAssignableFrom(clazz);
        boolean nameEndsWithJob = clazz.getName().endsWith(DefaultGrailsJobClass.JOB);
        return assignableFrom && nameEndsWithJob;
    }
}
