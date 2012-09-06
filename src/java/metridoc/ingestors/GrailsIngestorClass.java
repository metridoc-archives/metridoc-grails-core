package metridoc.ingestors;

import groovy.util.ConfigObject;
import org.codehaus.groovy.grails.commons.InjectableGrailsClass;

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 9/5/12
 * Time: 1:56 PM
 * To change this template use File | Settings | File Templates.
 */
public interface GrailsIngestorClass extends InjectableGrailsClass {

    void configure();
}
