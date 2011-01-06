/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package metridoc.component.poll;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.camel.Component;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;

/**
 *
 * @author tbarker
 */
public class PollEndpoint extends DefaultEndpoint{

    private static final String VALID_PATTERN_TEXT = "^\\w+:/?/?(\\w+:.*)";
    protected static final Pattern VALID_PATTERN = Pattern.compile(VALID_PATTERN_TEXT);
    protected static final String ERROR_MESSAGE = "%s is not a valid endpoint for poll component, must be in the form "
            + VALID_PATTERN_TEXT;

    public PollEndpoint(String endpointUri, Component component) {
        super(endpointUri, component);
        validateEndpoint(endpointUri);
    }

    @Override
    public Producer createProducer() throws Exception {
        return new PollProducer(this);
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    protected static void validateEndpoint(String endpointUri) {
        Matcher m = VALID_PATTERN.matcher(endpointUri);

        if (!m.matches()) {
            String message = String.format(ERROR_MESSAGE, endpointUri);
            throw new IllegalArgumentException(message);
        }
    }

}
