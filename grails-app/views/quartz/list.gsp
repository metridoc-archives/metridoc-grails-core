<%--
  Created by IntelliJ IDEA.
  User: tbarker
  Date: 8/24/12
  Time: 11:23 AM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<md:report>
    <g:if test="${workflows}">
        <table>
            <thead>
            <tr>
                <g:sortableColumn property="name" title="Workflows"/>
                <th class="centeredContent">Previous Run</th>
                <th class="centeredContent">Next Run</th>
            </tr>

            </thead>
            <tbody>
                <g:each in="${workflows}" var="workflow" status="i">
                    <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
                        <td>${workflow.name}</td>
                        <td class="centeredContent">${workflow.previousFireTime}</td>
                        <td class="centeredContent">${workflow.nextFireTime}</td>
                    </tr>
                </g:each>
            </tbody>
        </table>
    </g:if>
    <g:else>
    </g:else>
</md:report>