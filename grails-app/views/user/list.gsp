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
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'shiroUser.label', default: 'ShiroUser')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-shiroUser" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div id="list-shiroUser" class="content scaffold-list" role="main">
			<h1>User List</h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
				<thead>
					<tr>
					
						<g:sortableColumn property="username" title="${message(code: 'shiroUser.username.label', default: 'Username')}" />
					

					</tr>
				</thead>
				<tbody>
				<g:each in="${shiroUserInstanceList}" status="i" var="shiroUserInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${shiroUserInstance.id}">${fieldValue(bean: shiroUserInstance, field: "username")}</g:link></td>
					

					</tr>
				</g:each>
            </tbody>
			</table>

			<div class="pagination">
				<g:paginate total="${shiroUserInstanceTotal}" />
			</div>

            <div id="newUserButton">
                <span class="buttons">
                    <g:link controller="user" action="create">
                        New User
                    </g:link>
                </span>
            </div>
		</div>
	</body>
</html>
