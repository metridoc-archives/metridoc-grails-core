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

    <g:render template="/user/tabs" plugin="metridocCore"/>

    <div id="edit-shiroUser" class="content scaffold-edit" role="main">
        <h1><g:message code="default.edit.label" args="['User']" default="Edit User"/></h1>
        <g:if test="${flash.message}">
            <div class="message" role="status">${flash.message}</div>
        </g:if>

        <g:hasErrors bean="${shiroUserInstance}">
            <ul class="errors" role="alert">
                <g:renderErrors bean="${shiroUserInstance}" as="list" />
            </ul>
        </g:hasErrors>


        <g:form method="post">
            <g:hiddenField name="id" value="${shiroUserInstance?.id}"/>
            <g:hiddenField name="version" value="${shiroUserInstance?.version}"/>

            <fieldset class="form">
                <g:render template="form"/>
            </fieldset>

            <fieldset class="buttons">
                <g:actionSubmit class="save" action="update"
                                value="${message(code: 'default.button.update.label', default: 'Update')}"/>
                <g:if test="${shiroUserInstance.username != currentUserName && shiroUserInstance.username != 'admin'}">
                    <g:actionSubmit class="delete" action="delete"
                                    value="${message(code: 'default.button.delete.label', default: 'Delete')}"
                                    formnovalidate=""
                                    onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');"/>
                </g:if>
            </fieldset>

        </g:form>
    </div>
</md:report>