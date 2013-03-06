<%--
  Created by IntelliJ IDEA.
  User: weizhuowu
  Date: 9/10/12
  Time: 3:19 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<md:report module="quartzShow">
    <md:header>Trigger Details</md:header>

    <g:form class="form-horizontal" action="updateSchedule" id="${triggerName}">
        <div class="control-group">
            <label for="triggerName" class="control-label">Trigger Name:</label>

            <div class="controls">
                <input name="triggerName" type="text" disabled="" value="${triggerName}"/>
            </div>
            <label for="nextFireTime" class="control-label">Next Fire Time:</label>

            <div class="controls">
                <input name="nextFireTime" type="text" disabled="" value="${nextFireTime}"/>
            </div>
            <label for="availableSchedules" class="control-label">Available Schedules:</label>

            <div class="controls">
                <g:select name="availableSchedules" from="${availableSchedules}" value="${currentSchedule}"/>
            </div>

            <label for="arguments" class="control-label">Arguments:</label>
            <div class="controls">
                <input id="arguments" name="arguments" type="text" value="${arguments}"/>
            </div>

            <label for="config" class="control-label">Configuration (url or groovy):</label>
            <div class="controls">
                <g:textArea name="config" id="code" value="${config}"/>
            </div>

            <div class="controls">
                <button id="updateScheduleBtn" type="submit" class="btn" disabled=""><i class="icon-edit"></i> Update
                </button>
            </div>

        </div>
    </g:form>

    <g:if test="${jobLog}">
        <md:header>Job Log</md:header>
        <md:outputLogFile fileBody="${jobLog}"/>
    </g:if>
</md:report>
