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
            <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label" default="Home"/></a></li>
            <li><g:link class="list" controller="user" action="list"><g:message code="default.list.label" args="['User']" default="User List"/></g:link></li>
            <li><g:link class="create" controller="user" action="create"><g:message code="default.create.label" args="['User']" default="Create User"/></g:link></li>
            <li><g:link class="list" controller="role" action="list"><g:message code="default.list.label" args="['Role']" default="Role List"/></g:link></li>
            <li><g:link class="create" controller="role" action="create"><g:message code="default.create.label" args="['Role']" default="Create Role"/></g:link></li>
        </ul>
    </div>

    <div id="show-shiroUser" class="content scaffold-show" role="main">
        <h1><g:message code="default.show.label" args="['User']" default="Show User"/></h1>
        <g:if test="${flash.message}">
            <div class="message" role="status">${flash.message}</div>
        </g:if>
        <ol class="property-list shiroUser">

            <g:if test="${shiroUserInstance?.username}">
                <li class="fieldcontain">
                    <span id="username-label" class="property-label"><g:message code="shiroUser.username.label"
                                                                                default="Username"/></span>

                    <span class="property-value" aria-labelledby="username-label"><g:fieldValue
                            bean="${shiroUserInstance}"
                            field="username"/></span>

                </li>
            </g:if>

            <g:if test="${shiroUserInstance?.emailAddress}">
                <li class="fieldcontain">
                    <span id="emailAddress-label" class="property-label"><g:message code="shiroUser.emailAddress.label"
                                                                                    default="EmailAddress"/></span>
                    <span class="property-value" aria-labelledby="emailAddress-label"><g:fieldValue field="emailAddress"
                                                                                                    bean="${shiroUserInstance}"/></span>
                </li>
            </g:if>

            <g:if test="${shiroUserInstance?.roles}">
                <li class="fieldcontain">
                    <span id="roles-label" class="property-label"><g:message code="shiroUser.roles.label"
                                                                             default="Roles"/></span>

                    <g:each in="${shiroUserInstance.roles}" var="r">
                        <span class="property-value" aria-labelledby="roles-label"><g:link controller="Role"
                                                                                           action="show"
                                                                                           id="${r.id}">${r.name}</g:link></span>
                    </g:each>

                </li>
            </g:if>

        </ol>
        <g:form>
            <fieldset class="buttons">
                <g:hiddenField name="id" value="${shiroUserInstance?.id}"/>
                <g:if test="${shiroUserInstance.username!='anonymous'}">
                    <g:link class="edit" action="edit" id="${shiroUserInstance?.id}"><g:message
                            code="default.button.edit.label"
                            default="Edit"/></g:link>
                    <g:if test="${shiroUserInstance.username != currentUserName && shiroUserInstance.username != 'admin'}">
                    <g:actionSubmit class="delete" action="delete"
                                    value="${message(code: 'default.button.delete.label', default: 'Delete')}"
                                    onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');"/>
                    </g:if>
                    </g:if>
            </fieldset>
        </g:form>
    </div>

</md:report>