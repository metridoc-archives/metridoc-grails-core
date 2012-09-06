package metridoc.ingestors;

import org.codehaus.groovy.grails.commons.ArtefactHandlerAdapter;

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 9/5/12
 * Time: 1:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class IngestorArtefactHandler extends ArtefactHandlerAdapter {

    public static final String TYPE = "Ingestor";

    public IngestorArtefactHandler() {
        //type is the same as suffix, abstract classes are allowed
        super(TYPE, GrailsIngestorClass.class, DefaultGrailsIngestorClass.class, TYPE, true);
    }
}
