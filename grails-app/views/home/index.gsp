<!--

    Copyright 2010 Trustees of the University of Pennsylvania Licensed under the
    Educational Community License, Version 2.0 (the "License"); you may
    not use this file except in compliance with the License. You may
    obtain a copy of the License at

    http://www.osedu.org/licenses/ECL-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on an "AS IS"
    BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
    or implied. See the License for the specific language governing
    permissions and limitations under the License.

-->
<!doctype html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <link rel="stylesheet" type="text/css" href="http://yui.yahooapis.com/3.5.1/build/cssgrids/grids-min.css">
    </head>

    <body>

        <div id="page-body" role="main" style="padding: 50px; padding-left: 0px">

            <h1>Welcome to MetriDoc</h1>

            <p>
                MetriDoc is an extendable platform to view and maintain library statistics and reports.
                <shiro:isNotLoggedIn>
                    You must <g:link controller="auth" action="index">login</g:link> to view available applications.
                </shiro:isNotLoggedIn>
            </p>

            <shiro:isLoggedIn>
                <div id="application-list" role="navigation">
                    <g:if test="${applications.size()}">
                        <div class="yui3-g">
                            <div class="yui3-u-1-2">
                                <h2>Available Applications</h2>
                                <ul>
                                    <g:if test="${reports}">
                                        <g:each var="c" in="${reports}">
                                            <li class="application"><g:link controller="${c.value}">${c.key}</g:link></li>
                                        </g:each>
                                    </g:if>
                                    <g:else>
                                        <li class = "application">No reports available</li>
                                    </g:else>
                                </ul>
                            </div>

                            <g:if test="${administrativeApps}">
                                <div class="yui3-u-1-2">
                                    <h2>Administration</h2>
                                    <ul>
                                        <g:each var="c"
                                                in="${administrativeApps}">
                                            <li class="application"><g:link
                                                    controller="${c.value}">${c.key}</g:link></li>
                                        </g:each>
                                    </ul>
                                </div>
                            </g:if>
                        </div>

                    </g:if>
                    <g:else>
                        <p>
                            Please <g:link controller="auth" action="index">login</g:link> to view available applications.
                        </p>
                    </g:else>
                </div>
            </shiro:isLoggedIn>
        </div>
    </body>
</html>
