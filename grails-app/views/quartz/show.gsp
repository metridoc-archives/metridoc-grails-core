<%--
  Created by IntelliJ IDEA.
  User: weizhuowu
  Date: 9/10/12
  Time: 3:19 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<md:report>
    <md:header>Trigger Details</md:header>

    <g:form class="form-horizontal" action="updateSchedule" id="${triggerName}">
        <div class="control-group">
            <label for="triggerName" class="control-label">Trigger Name:</label>
            <div class="controls">
                <input name="triggerName" type="text" disabled="" value="${triggerName}" />
            </div>
            <label for="nextFireTime" class="control-label">Next Fire Time:</label>
            <div class="controls">
                <input name="nextFireTime" type="text" disabled="" value="${nextFireTime}" />
            </div>
            <label for="availableSchedules" class="control-label">Available Schedules:</label>
            <div class="controls">
                <g:select name="availableSchedules" from="${availableSchedules}" value="${currentSchedule}"/>
            </div>
            <div class="controls">
                <button id="updateScheduleBtn" type="submit" class="btn" disabled=""><i class="icon-edit"></i> Update</button>
            </div>
        </div>
    </g:form>
    <r:external dir="quartz/js" plugin="metridoc-core" file="show.js" disposition="body"/>
</md:report>
