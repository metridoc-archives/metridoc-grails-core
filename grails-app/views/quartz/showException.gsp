<%--
  Created by IntelliJ IDEA.
  User: weizhuowu
  Date: 9/14/12
  Time: 4:11 PM
  To change this template use File | Settings | File Templates.
--%>
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

    <h1><g:message code="default.job.label" args="[capitalizedWorkflowName]" default="Job"/></h1>

    <div id="show-workflow" class="content scaffold-show" role="main">
    <g:render template="exception"/>
    </div>
</md:report>