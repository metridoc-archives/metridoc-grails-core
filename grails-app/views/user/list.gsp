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

    <g:render template="/user/tabs" plugin="metridocCore"/>

    <div id="list-shiroUser" class="content scaffold-list" role="main">
        <table class="table table-striped table-hover">
            <thead>
            <tr>
                <g:sortableColumn property="username"
                                  title="Users"/>
            </tr>
            </thead>

            <tbody>
            <g:each in="${shiroUserInstanceList}" status="i" var="shiroUserInstance">
                <tr>

                    <td>
                        <g:link action="show"
                                id="${shiroUserInstance.id}">${fieldValue(bean: shiroUserInstance, field: "username")}
                        </g:link>
                        <g:if test="${shiroUserInstance.username != 'anonymous'}">
                            <span class="inCellActions">

                                <a href="edit/${shiroUserInstance.id}">
                                    <i class="icon-edit"></i>
                                </a>
                                <g:if test="${shiroUserInstance.username != currentUserName}">
                                    <a class="delete" href="#" onclick="deleteUser(${shiroUserInstance.id})">
                                        <i class="icon-trash"></i>
                                    </a>
                                </g:if>
                                <g:form name="mdForm_${shiroUserInstance.id}" method="delete" action="delete"
                                        id="${shiroUserInstance.id}"/>

                            </span>
                        </g:if>
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