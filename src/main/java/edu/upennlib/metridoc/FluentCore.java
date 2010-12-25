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

package edu.upennlib.metridoc;

import edu.upennlib.metridoc.routes.MetridocRouteTemplate;
import edu.upennlib.metridoc.impl.routes.ResolverTemplate;
import edu.upennlib.metridoc.url.UrlBuilder;
import edu.upennlib.metridoc.url.UriName;
import edu.upennlib.metridoc.impl.url.BeanUrlBuilder;
import edu.upennlib.metridoc.impl.url.DirectUrlBuilder;
import edu.upennlib.metridoc.impl.url.Mock;
import edu.upennlib.metridoc.impl.url.FileUrlBuilder;
import edu.upennlib.metridoc.impl.url.LogUrlBuilder;
import edu.upennlib.metridoc.impl.url.SedaUrlBuilder;
import edu.upennlib.metridoc.impl.url.SqlPipelineBuilder;
import edu.upennlib.metridoc.impl.url.TimerUrlBuilder;
import edu.upennlib.metridoc.url.PipelineBuilder;
import java.util.HashMap;
import java.util.Map;
import org.apache.camel.CamelContext;
import org.apache.camel.spi.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Thomas Barker
 */
public class FluentCore {
    
    public static final String EVENT_TYPE = "Metridoc.EventType";
    public static final String EVENT_DESCRIPTION = "Metridoc.EventDescription";
    public static final String SYNC_GROUP_DONE = "Metridoc.SyncGroup.Done";
    public static final String SYNC_GROUP_START = "Metridoc.SyncGroup.Start";
    public static final String SYNC_GROUP_TIMEOUT = "Metridoc.SyncGroup.Timeout";
    public static final String SYNC_GROUP_TIMEOUT_UNITS = "Metridoc.SyncGroup.TimeoutUnits";
    public static final String SYNC_DESTINATION = "Metridoc.SyncDestination";
    public static final String ITERATOR_CREATOR_NAME = "Metridoc.IteratorCreator.Name";

    private static final Map<String, String> urlMap = new HashMap<String, String>();
    private static Registry registry;
    private static CamelContext camelContext;
    private static Logger LOGGER = LoggerFactory.getLogger(FluentCore.class);
    
    public static CamelContext getCamelContext() {
        return camelContext;
    }

    public static void setCamelContext(CamelContext camelContext) {
        FluentCore.camelContext = camelContext;
    }
    
    public static Registry getRegistry() {
        return registry;
    }

    public static void setRegistry(Registry registry) {
        FluentCore.registry = registry;
    }
    
    public static String url(UrlBuilder urlBuilder) {
        return urlBuilder.url();
    }
    
    public static String[] urls(PipelineBuilder pipelineBuilder) {
        return pipelineBuilder.urls();
    }
    
    public static DirectUrlBuilder direct(String name) {
        return new DirectUrlBuilder().name(name);
    }
    
    public static Mock mock(String name) {
        return new Mock().name(name);
    }
    
    public static FileUrlBuilder file(String folderName) {
        return new FileUrlBuilder().fromFolder(folderName);
    }
    
    public static BeanUrlBuilder beanUrl(String serviceName, String method) {
        return new BeanUrlBuilder().name(serviceName).method(method);
    }
    
    public static String url(UriName name) {
        return urlMap.get(name.name());
    }
    
    public static void registerUrl(UriName name, UrlBuilder urlBuilder) {
        register(name, urlBuilder.url());
    }

    public static void register(UriName name, String url) {
        
        if (LOGGER.isInfoEnabled()) {
            String message = "registering url %s with name %s";
            LOGGER.info(String.format(message, url, name.name()));
        }

        urlMap.put(name.name(), url);
    }
    
    public String getUrl(UriName name) {
        return urlMap.get(name.name());
    }
    
    public static SqlPipelineBuilder sql() {
        return new SqlPipelineBuilder();
    }
    
    public static TimerUrlBuilder startUpJob() {
        return new TimerUrlBuilder();
    }
    
    public static SedaUrlBuilder seda(String name) {
        return new SedaUrlBuilder().name(name);
    }

    public static MetridocRouteTemplate<ResolverTemplate> resolvers(String... resolvers) {
        return new ResolverTemplate().resolvers(resolvers);
    }

    public static LogUrlBuilder log(String name) {
        return new LogUrlBuilder().name(name);
    }
    
}
