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


    <div class="nav" role="navigation">
        <ul>
            <li><g:link class="home" controller="home" action="index">Home</g:link></li>
            <li><g:link class="list" action="list">User List</g:link></li>
        </ul>
    </div>

    <div id="create-shiroUser" class="content scaffold-create" role="main">
        <h1>Create User</h1>
        <g:if test="${flash.message}">
            <div class="message" role="status">${flash.message}</div>
        </g:if>
        <g:hasErrors bean="${shiroUserInstance}">
            <ul class="errors" role="alert">
                <g:eachError bean="${shiroUserInstance}" var="error">
                    <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message
                            error="${error}"/></li>
                </g:eachError>
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

