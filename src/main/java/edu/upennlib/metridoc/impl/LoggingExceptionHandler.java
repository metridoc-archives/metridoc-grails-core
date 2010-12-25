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

import edu.upennlib.metridoc.ExceptionHandler;
import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.apache.camel.spi.ExchangeFormatter;
import org.apache.commons.lang.text.StrBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author tbarker
 */
public class LoggingExceptionHandler implements ExceptionHandler{

    private ExchangeFormatter formatter;
    private Logger LOGGER = LoggerFactory.getLogger(LoggingExceptionHandler.class);

    public ExchangeFormatter getFormatter() {
        return formatter;
    }

    public void setFormatter(ExchangeFormatter formatter) {
        this.formatter = formatter;
    }

    @Override
    @Handler
    public void handleBadExchange(Exchange exchange) {
        Exception exception = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
        String logMessage = "exchange contained an error:";
        String exchangeLogMessage = exchangeMessage(logMessage, exchange);
        handleException(exchangeLogMessage, exception);
    }

    @Override
    public void handleException(String message, Exception exception) {
        LOGGER.error(message, exception);
    }

    protected String exchangeMessage(String logMessage, Exchange exchange) {
        return new StrBuilder(logMessage)
                    .appendNewLine()
                    .append(getFormattedExchange(exchange))
                    .toString();
    }

    protected String getFormattedExchange(Exchange exchange) {
        return (String) formatter.format(exchange);
    }
}