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
<%@ page import="metridoc.reports.ShiroUser" %>
<md:report>

    <g:set var="entityName" value="${'User'}"/>
    <div class="nav" role="navigation">
        <ul>
            <li><g:link class="home" controller="home" action="index"><g:message code="default.home.label"
                                                                                 default="Home"/></g:link></li>
            <li><g:link class="list" controller="user" action="list"><g:message code="default.list.label"
                                                                                args="['User']"
                                                                                default="User List"/></g:link></li>
            <li><g:link class="list" controller="role" action="list"><g:message code="default.list.label"
                                                                                args="['Role']"
                                                                                default="Role List"/></g:link></li>
            <li><g:link class="create" controller="role" action="create"><g:message code="default.create.label"
                                                                                    args="['Role']"
                                                                                    default="Create Role"/></g:link></li>
        </ul>
    </div>

    <div id="create-shiroUser" class="content scaffold-create" role="main">
        <h1><g:message code="default.create.label" args="['User']" default="Create User"/></h1>
        <g:if test="${flash.message}">
            <div class="message" role="status">${flash.message}</div>
        </g:if>

    %{--TODO: render Error is not userFriendly, need to convert a way to display messages--}%
        <g:if test="${'metridoc-core'.equals(grailsApplication.metadata.getAt("app.name"))}">
            <g:hasErrors bean="${shiroUserInstance}">
                <ul class="errors" role="alert">
                    <g:renderErrors bean="${shiroUserInstance}" as="list"/>
                </ul>
            </g:hasErrors>
        </g:if>


        <g:form action="save">
            <fieldset class="form">
                <g:render template="form"/>
            </fieldset>
            <fieldset class="buttons">
                <g:submitButton name="create" class="save"
                                value="${message(code: 'default.button.create.label', default: 'Create')}"/>
            </fieldset>
        </g:form>

    </div>

</md:report>

