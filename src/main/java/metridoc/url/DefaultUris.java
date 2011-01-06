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

package metridoc.url;
import static metridoc.FluentCore.*;

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
