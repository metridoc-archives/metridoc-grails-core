/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.upennlib.metridoc.impl.routes;

import edu.upennlib.metridoc.impl.url.LogUrlBuilder.Level;
import static edu.upennlib.metridoc.url.DefaultUris.*;
import static edu.upennlib.metridoc.FluentCore.*;
import org.apache.camel.builder.RouteBuilder;

/**
 *
 * @author tbarker
 */
public class ExceptionHandlerRoute extends RouteBuilder{

    @Override
    public void configure() throws Exception {
        from(url(EXCEPTION_HANDLER)).to(url(log("metridocExceptionLogger").level(Level.ERROR)));
    }
}
