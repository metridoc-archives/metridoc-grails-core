package metridoc.ingestors;

import org.codehaus.groovy.grails.commons.AbstractInjectableGrailsClass;

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 9/5/12
 * Time: 1:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class DefaultGrailsIngestorClass extends AbstractInjectableGrailsClass implements GrailsIngestorClass{
    public DefaultGrailsIngestorClass(Class wrappedClass) {
        super(wrappedClass, IngestorArtefactHandler.TYPE);
    }
}
