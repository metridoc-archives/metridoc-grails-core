<%@ page import="org.apache.shiro.SecurityUtils" %>
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
<!--[if lt IE 7 ]> <html lang="en" class="no-js ie6"> <![endif]-->
<!--[if IE 7 ]>    <html lang="en" class="no-js ie7"> <![endif]-->
<!--[if IE 8 ]>    <html lang="en" class="no-js ie8"> <![endif]-->
<!--[if IE 9 ]>    <html lang="en" class="no-js ie9"> <![endif]-->
<!--[if (gt IE 9)|!(IE)]><!--> <html lang="en" class="no-js"><!--<![endif]-->
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
        <title>MetriDoc</title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <r:require module="application"/>
        <g:layoutHead/>
        <r:layoutResources/>
    </head>

    <body>
        <div id="doc4">
            <div id="metridocBanner" role="banner">
                <a id="metridocLogo" href="http://metridoc.googlecode.com">
                    <img src="${resource(dir: 'images', file: 'MDlogo_small.png')}" alt="MetriDoc"/>
                </a>

                <% if (SecurityUtils.subject.principal == "anonymous" || !SecurityUtils.subject.isAuthenticated()) { %>
                <a id="metridocLoginLink" href="/<g:meta name="app.name"/>/auth">login</a>
                <% } else { %>
                <a id="metridocLoginLink" href="/<g:meta name="app.name"/>/auth/signOut">logout</a>
                <% } %>
            </div>

            <div id="metridocNavigation">
                <ul>
                    <li>
                        <strong>
                            <a href="/<g:meta name="app.name"/>">Home</a>
                        </strong>
                    </li>
                    <% if ("admin" == SecurityUtils.subject.principal) { %>
                    <li>
                        <strong>
                            <a href="/<g:meta name="app.name"/>/admin">Settings</a>
                        </strong>
                    </li>
                    <% } %>
                </ul>
            </div>
            <g:layoutBody/>
            <div class="footer" role="contentinfo"></div>

            <div id="spinner" class="spinner" style="display:none;">
                <g:message code="spinner.alt" default="Loading&hellip;"/>
            </div>

            <r:layoutResources/>
        </div>
    </body>
</html>