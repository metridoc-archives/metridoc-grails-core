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
<%@ page contentType="text/html;charset=UTF-8" %>
<md:report>

    <div class="nav" role="navigation">
        <ul>
            <li><g:link class="home" controller="home" action="index"><g:message code="default.home.label" default="Home"/></g:link></li>
            <li><g:link class="create" action="create"><g:message code="default.create.label" args="['User']" default="Create User"/></g:link></li>
        </ul>
    </div>
%{--<g:set var="entityName" value="${message(code: 'shiroUser.label', default: 'ShiroUser')}"/>--}%
    <div id="list-shiroUser" class="content scaffold-list" role="main">

        <g:if test="${flash.message}">
            <div class="message" role="status">${flash.message}</div>
        </g:if>
        <table>
            <thead>
                <tr>

                    <g:sortableColumn property="username"
                                      title="Users"/>

                </tr>
            </thead>
            <tbody>
                <g:each in="${shiroUserInstanceList}" status="i" var="shiroUserInstance">
                    <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">

                        <td>
                            <g:link action="show"
                                    id="${shiroUserInstance.id}">${fieldValue(bean: shiroUserInstance, field: "username")}
                            </g:link>
                            <span class="inCellActions">
                                <a href="edit/${shiroUserInstance.id}">
                                    <r:img uri="/images/skin/database_edit.png"/>
                                </a>
                                <a class="delete" href="#" onclick="deleteUser(${shiroUserInstance.id})">
                                    <r:img uri="/images/skin/database_delete.png"/>
                                </a>
                                <g:form name="mdForm_${shiroUserInstance.id}" method="delete" action="delete" id="${shiroUserInstance.id}"/>
                            </span>
                        </td>

                    </tr>
                </g:each>
            </tbody>
        </table>

        <g:if test="${showPagination}">
            <div class="pagination">
                <g:paginate total="${shiroUserInstanceTotal}"/>
            </div>
        </g:if>

    </div>
</md:report>