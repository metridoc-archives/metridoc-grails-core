<!DOCTYPE html>
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
    <g:javascript library="jquery"/>
    <r:require module="application"/>
    <g:layoutHead/>
    <r:layoutResources/>
</head>

<body>
<div id="doc4" class="shadow">
    <div id="metridocBanner" role="banner">
        <a id="metridocLogo" href="http://metridoc.googlecode.com">
            <img src="${resource(plugin: 'metridocCore', dir: 'images', file: 'MDlogo_small.png')}"
                 alt="MetriDoc"/>
        </a>

        <% if (SecurityUtils.subject.principal == "anonymous" || !SecurityUtils.subject.isAuthenticated()) { %>
        <a id="metridocLoginLink" href="/<g:meta name="app.name"/>/auth">login</a>
        <% } else { %>
        <span id="metridocLoginLink">
            <a href="/<g:meta name="app.name"/>/profile/edit">${SecurityUtils.subject.principal}</a> (<a
                href="/<g:meta name="app.name"/>/auth/signOut">logout</a>)
        </span>
        <% } %>

    </div>

    <div id="metridocNavigationBar" class="ui-widget-header">
        <ul id="metridocNavigation" class="shadow">
            <li>
                <a href="/<g:meta name="app.name"/>/home" class="ui-widget-header">Home</a>
            </li>
            <shiro:authenticated>
                <% if (SecurityUtils.subject.principal != "anonymous" && SecurityUtils.subject.isAuthenticated()) { %>
                <li>
                    %{--<g:link controller="profile" action="index">Settings</g:link>--}%
                    <a href="#">Settings</a>
                    <ul class="shadow">
                        <li>
                            <g:link controller="profile" action="index" class="userAccount menu-item">Account</g:link>
                        </li>

                        <% if (SecurityUtils.subject.hasRole("ROLE_ADMIN")) { %>
                            <li>
                                <g:link controller="user" action="list" class="users menu-item">Manage Users</g:link>
                            </li>
                            <li>
                                <g:link controller="log" action="index" class="log menu-item">Application Log</g:link>
                            </li>
                            <li>
                                <g:link controller="quartz" action="index" class="quartz menu-item">Job List</g:link>
                            </li>
                        <% } %>
                    </ul>
                </li>
                <% } %>
            </shiro:authenticated>
        </ul>
    </div>
    <g:layoutBody/>

    <div id="spinner" class="spinner" style="display:none;">
        <g:message code="spinner.alt" default="Loading&hellip;"/>
    </div>

    <r:layoutResources/>
</div>
</body>
</html>