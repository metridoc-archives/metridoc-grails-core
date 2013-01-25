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
<%@ page import="metridoc.reports.ShiroRole" %>
<md:report>

    <g:set var="entityName" value="${'Role'}"/>
    <g:render template="/user/tabs" plugin="metridocCore"/>

    <div id="create-shiroRole" class="content scaffold-create" role="main">
        <h1><g:message code="default.create.label" args="['Role']" default="Create Role"/></h1>
        <g:if test="${flash.message}">
            <div class="message" role="status">${flash.message}</div>
        </g:if>

        <g:hasErrors bean="${shiroRoleInstance}">
            <ul class="errors" role="alert">
                <g:renderErrors bean="${shiroRoleInstance}" as="list"/>
            </ul>
        </g:hasErrors>

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

