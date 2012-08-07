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
<%@ page contentType="text/html;charset=UTF-8" %>
<md:report>

    <div class="nav" role="navigation">
        <ul>
            <li><g:link class="home" controller="home" action="index">
                <g:message code="default.home.label" default="Home"/>
            </g:link></li>
            <li><g:link class="create" controller="role" action="create"><g:message code="default.create.label" args="['Role']" default="Create Role"/></g:link></li>
            <li><g:link class="list" controller="user" action="list"><g:message code="default.list.label" args="['User']" default="User List"/></g:link></li>
            <li><g:link class="create" controller="user" action="create"><g:message code="default.create.label" args="['User']" default="Create User"/></g:link></li>
        </ul>
    </div>

    <div id="list-shiroRole" class="content scaffold-list" role="main">

        <g:if test="${flash.message}">
            <div class="message" role="status">${flash.message}</div>
        </g:if>
        <table>
            <thead>
            <tr>
                <g:sortableColumn property="name"
                                  title="Roles"/>
            </tr>
            </thead>
            <tbody>
            <g:each in="${shiroRoleInstanceList}" status="i" var="shiroRoleInstance">
                <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">

                    <td>
                        <g:link action="show"
                                id="${shiroRoleInstance.id}">${fieldValue(bean: shiroRoleInstance, field: "name")}
                        </g:link>
                        <span class="inCellActions">

                            <a href="edit/${shiroRoleInstance.id}">
                                <r:img uri="/images/skin/database_edit.png"/>
                            </a>

                            <a class="delete" href="#" onclick="deleteRole(${shiroRoleInstance.id})">
                                <r:img uri="/images/skin/database_delete.png"/>
                            </a>

                            <g:form name="mdForm_${shiroRoleInstance.id}" method="delete" action="delete"
                                    id="${shiroRoleInstance.id}"/>
                        </span>
                    </td>
                </tr>
            </g:each>
            </tbody>
        </table>

        <g:if test="${showPagination}">
            <div class="pagination">
                <g:paginate total="${shiroRoleInstanceTotal}"/>
            </div>
        </g:if>

    </div>
</md:report>
