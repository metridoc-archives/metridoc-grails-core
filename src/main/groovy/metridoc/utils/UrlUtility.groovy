/*
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
package metridoc.utils

import metridoc.plugins.Plugin

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 11/11/11
 * Time: 11:34 AM
 */
class UrlUtility {

    static UrlWrapper wrapUrl(String url) {

        def result

        if(isUrl(url)) {
            result = new UrlWrapper(url: url)
        } else {
            result = new UrlWrapper(pathAndQuery: url)
        }

        return result

    }

    private static boolean isUrl(String url) {
        try {
            new URL(url) //throws exception if it is not a url
            return true
        } catch(MalformedURLException ex) {
            return false
        }
    }

}

class UrlWrapper {
    String pathAndQuery
    String url
    private Map<String, String> _queryMap

    String getHost() {
        def urlType = new URL(url)
        return urlType.host
    }

    String getPath() {

        def result

        if (pathAndQuery) {
            result = pathAndQuery.split("\\?")[0]
        }

        if (url) {
            result = new URL(url).path
        }

        if (result && !result.startsWith("/")) {
            result = new StringBuilder("/").append(result)
        }

        if(result == StringUtils.EMPTY) {
            result = null
        }

        return result
    }

    Map<String, String> getQueryMap() {
        if(_queryMap) {
            return _queryMap
        }

        _queryMap = [:]
        def queryPairs = getQuery().split("&")
        queryPairs.each {
            def pair = it.split("=")
            if(pair.size() > 1) {
                _queryMap[pair[0]] = java.net.URLDecoder.decode(pair[1], "utf8")
            } else {
                _queryMap[pair[0]] = null
            }
        }

        return _queryMap
    }

    String getQuery() {

        if(url) {
            return new URL(url).query
        }

        def indexOfBeginningOfQuery = pathAndQuery.indexOf('?')

        if(indexOfBeginningOfQuery > 0) {
            return pathAndQuery.substring(indexOfBeginningOfQuery + 1)
        }
    }

    String getPropertyValue(String propertyName) {
        getQueryMap()[propertyName]
    }

    boolean containsProperty(String propertyName) {
        getQueryMap().containsKey(propertyName)
    }
}
