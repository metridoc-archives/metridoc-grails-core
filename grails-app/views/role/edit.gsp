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

    <div class="nav" role="navigation">
        <ul>
            <li><g:link class="home" controller="home" action="index">
                <g:message code="default.home.label" default="Home"/>
            </g:link></li>
            <li><g:link class="list" controller="role" action="list">
                <g:message code="default.list.label" args="['Role']" default="Role List"/>
            </g:link></li>
            <li><g:link class="create" controller="role" action="create">
                <g:message code="default.create.label" args="['Role']" default="Create Role"/>
            </g:link></li>
            <li><g:link class="list" controller="user" action="list">
                <g:message code="default.list.label" args="['User']" default="User List"/>
            </g:link></li>
            <li><g:link class="create" controller="user" action="create">
                <g:message code="default.create.label" args="['User']" default="Create User"/>
            </g:link></li>
        </ul>
    </div>


    <div id="edit-shiroRole" class="content scaffold-edit" role="main">
        <h1><g:message code="default.edit.label" args="['Role']" default="Edit Role"/></h1>
        <g:if test="${flash.message}">
            <div class="message" role="status">${flash.message}</div>
        </g:if>
        <g:hasErrors bean="${shiroRoleInstance}">
            <ul class="errors" role="alert">
                <g:eachError bean="${shiroRoleInstance}" var="error">
                    <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message
                            error="${error}"/></li>
                </g:eachError>
            </ul>
        </g:hasErrors>
        <g:form method="post">

            <g:hiddenField name="id" value="${shiroRoleInstance?.id}"/>
            <g:hiddenField name="version" value="${shiroRoleInstance?.version}"/>

            <fieldset class="form">
                <g:render template="form"/>
            </fieldset>

            <fieldset class="buttons">
                <g:actionSubmit class="save" action="update"
                                value="${message(code: 'default.button.update.label', default: 'Update')}"/>
            </fieldset>

        </g:form>
    </div>
</md:report>