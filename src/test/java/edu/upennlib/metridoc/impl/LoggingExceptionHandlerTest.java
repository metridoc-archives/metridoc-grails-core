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

package edu.upennlib.metridoc.impl;

import edu.upennlib.metridoc.test.StandardTest;
import org.apache.camel.Exchange;
import org.apache.camel.spi.ExchangeFormatter;
import static org.easymock.EasyMock.*;
import org.junit.Test;

/**
 *
 * @author tbarker
 */
public class LoggingExceptionHandlerTest extends StandardTest{

    private Exchange exchange;
    private ExchangeFormatter formatter;
    private LoggingExceptionHandler handler;

    @Test
    public void formatterCalledWhenAnExchangeErrorOccurs() {
        replay(formatter(), exchange());
        handler().handleBadExchange(exchange());
        verify(formatter(), exchange());
    }
    
    @Test
    public void getFormattedExchangeReturnsInfoFromFormatter() {
        replay(formatter());
        assertEquals("foo bar", handler().getFormattedExchange(exchange()));
        verify(formatter());
    }

    @Test
    public void getMessageReturnsLogMessage_NewLine_ThenFormattedExchange() {
        replay(formatter());
        assertEquals("foo baz\nfoo bar", handler().exchangeMessage("foo baz", exchange()));
        verify(formatter());
    }

    public LoggingExceptionHandler handler() {
        if (handler == null) {
            handler = new LoggingExceptionHandler();
            handler.setFormatter(formatter());
        }

        return handler;
    }

    public Exchange exchange() {
        if (exchange == null) {
            exchange = createMock(Exchange.class);
            expect(exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class))
                    .andReturn(new RuntimeException("oops"));
        } 

        return exchange;
    }

    public ExchangeFormatter formatter() {
        if (formatter == null) {
            formatter = createMock(ExchangeFormatter.class);
            expect(formatter.format(exchange())).andReturn("foo bar");
        }
        return formatter;
    }





}