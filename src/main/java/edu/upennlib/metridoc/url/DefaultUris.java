/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.upennlib.metridoc.url;
import static edu.upennlib.metridoc.FluentCore.*;

/**
 *
 * @author Thomas Barker
 */
public enum DefaultUris implements UriName{
    EXCEPTION_HANDLER, EXCEPTION_STORAGE, METRIDOC_CHANNEL, METRIDOC_AGGREGATOR, METRIDOC_RESOLVER;
            
    static{
        registerUrl(DefaultUris.EXCEPTION_HANDLER, beanUrl("metridocExceptionHandler", "handle"));
        registerUrl(DefaultUris.METRIDOC_CHANNEL, direct("metridocChannel"));
        registerUrl(DefaultUris.METRIDOC_AGGREGATOR, direct("metridocAggregator"));
        registerUrl(DefaultUris.METRIDOC_RESOLVER, direct("metridocResolver"));
        registerUrl(DefaultUris.EXCEPTION_STORAGE, direct("jdbc:metridocDataSource"));
    }
}
