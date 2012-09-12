<%--
  Created by IntelliJ IDEA.
  User: tbarker
  Date: 8/24/12
  Time: 11:23 AM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<md:report>
    <div class="nav" role="navigation">
        <ul>
            <li><g:link class="home" controller="home" action="index"><g:message code="default.home.label"
                                                                                 default="Home"/></g:link></li>
            <li><g:link class="log" controller="log" action="index"><g:message code="default.log.label"
                                                                                default="Log"/></g:link></li>

        </ul>
    </div>
    <g:if test="${workflows}">
        <table>
            <thead>
            <tr>
                <th></th>
                <g:sortableColumn property="name" title="Workflows"/>
                <th class="centeredContent">Previous Run</th>
                <th class="centeredContent">Next Run</th>
                <th class="centeredContent">Last Duration</th>
            </tr>
            </thead>

            <tbody>
                <g:each in="${workflows}" var="workflow" status="i">
                    <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
                        <td class="centeredContent">
                            <g:if test="${workflow.lastException}">
                                <g:link action="exception" params="[id: workflow.unCapName]">
                                    <r:img plugin="metridoc-core" dir="images/skin" file="exclamation.png"/>
                                </g:link>
                            </g:if>
                            <g:if test="${workflow.running}">
                                <r:img plugin="metridoc-core" dir="images" file="spinner.gif"/>
                            </g:if>
                            <g:else>
                                <g:link params="[run: workflow.unCapName]">
                                    <r:img plugin="metridoc-core" dir="images/skin" file="media-playback-start.png"/>
                                </g:link>
                            </g:else>
                        </td>
                        <td><g:link action="show" params="[id: workflow.unCapName]">${workflow.name}</g:link></td>
                        <td class="centeredContent">${workflow.previousFireTime}</td>
                        <td class="centeredContent">${workflow.nextFireTime}</td>
                        <td class="centeredContent">${workflow.previousDuration}</td>
                    </tr>
                </g:each>
            </tbody>
        </table>
        <g:if test="${showPagination}">
            <div class="pagination">
                <g:paginate total="${workflowCount}"/>
            </div>
        </g:if>
    </g:if>
    <g:else>
    </g:else>
</md:report>