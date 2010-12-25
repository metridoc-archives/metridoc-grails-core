/**
 * Copyright 2010 Trustees of the University of Pennsylvania Licensed under the
 * Educational Community License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
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
