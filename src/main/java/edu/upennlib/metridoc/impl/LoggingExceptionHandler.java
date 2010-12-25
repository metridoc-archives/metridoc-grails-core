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