/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.upennlib.metridoc.routes;

import edu.upennlib.metridoc.url.DefaultUris;
import static edu.upennlib.metridoc.FluentCore.*;
import org.apache.camel.builder.RouteBuilder;

/**
 *
 * @author tbarker
 */
public abstract class MetridocRouteBuilder<T extends MetridocRouteBuilder> extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        errorHandler(deadLetterChannel(url(DefaultUris.EXCEPTION_HANDLER)));
        validateState();
        route();
    }

    public abstract void route() throws Exception;
    public abstract void validateState() throws IllegalStateException;


}
