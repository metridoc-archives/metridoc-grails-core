<%--
  Created by IntelliJ IDEA.
  User: weizhuowu
  Date: 9/10/12
  Time: 3:19 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<md:report>
    <div class="nav" role="navigation">
        <ul>
            <li><g:link class="home" controller="home" action="index"><g:message code="default.home.label"
                                                                                 default="Home"/></g:link></li>
            <li><g:link class="list" controller="quartz" action="list"><g:message code="default.list.label"
                                                                                     args="['Job']"
                                                                                     default="Job List"/></g:link></li>
            <li><g:link class="log" controller="log" action="index"><g:message code="default.log.label"
                                                                               default="Log"/></g:link></li>

        </ul>
    </div>

    <div id="show-workflow" class="content scaffold-show" role="main">
        <h1><g:message code="default.job.label" args="[workflow.capitalizedWorkflowName]" default="Job"/></h1>

        <ol class="property-list shiroUser">

            <g:if test="${workflow?.previousFireTime}">
                <li class="fieldcontain">
                    <span id="emailAddress-label" class="property-label">Previous Run</span>
                    <span class="property-value"
                          aria-labelledby="emailAddress-label">${workflow.previousFireTime}</span>
                </li>
            </g:if>

            <g:if test="${workflow?.nextFireTime}">
                <li class="fieldcontain">
                    <span id="roles-label" class="property-label">Next Run</span>
                    <span class="property-value" aria-labelledby="roles-label">${workflow.nextFireTime}</span>
                </li>
            </g:if>

            <g:if test="${workflow?.previousDuration}">
                <li class="fieldcontain">
                    <span id="label" class="property-label">Last Duration</span>
                    <span class="property-value" aria-labelledby="roles-label">${workflow.previousDuration}</span>
                </li>
            </g:if>

        </ol>

        <g:render template="exception"/>

    </div>

</md:report>
