package metridoc.schema;

import org.codehaus.groovy.grails.commons.ArtefactHandlerAdapter;

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 9/5/12
 * Time: 1:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class SchemaArtefactHandler extends ArtefactHandlerAdapter {

    public static final String TYPE = "Ingestor";

    public SchemaArtefactHandler() {
        super(TYPE, GrailsSchemaClass.class, DefaultGrailsSchemaClass.class, null);
    }
}
