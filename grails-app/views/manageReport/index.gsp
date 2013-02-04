<%--
  Created by IntelliJ IDEA.
  User: tbarker
  Date: 1/30/13
  Time: 2:37 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<md:report>
    <tmpl:manageReportHeaders/>
    <strong>Controller Specific Security:</strong>
    <table class="table table-striped table-hover">
        <tr>
            <th>Controller</th>
            <th>Protected?</th>
            <th>Has Roles?</th>
        </tr>
        <g:each in="${controllerDetails}" var="detail">
            <tr>
                <td><g:link action="show" params="[id: detail.key]">${detail.key}</g:link></td>

                <td>
                    <g:if test="${detail.value.isProtected}">
                        <i class="icon-check"></i>
                    </g:if>
                    <g:else>
                        <i class="icon-check-empty"></i>
                    </g:else>
                </td>

                <td>
                    <g:if test="${detail.value.roles}">
                        <i class="icon-check"></i>
                    </g:if>
                    <g:else>
                        <i class="icon-check-empty"></i>
                    </g:else>
                </td>
            </tr>
        </g:each>
    </table>
</md:report>