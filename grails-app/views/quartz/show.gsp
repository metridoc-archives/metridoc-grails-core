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
    <tmpl:editDescription/>
    <p id="description">${description} <!--suppress HtmlUnknownTarget -->
        <a href="#descriptionModal"
           id="editDescription"
           data-toggle="modal"
           data-original-title="Edit description"
           data-placement="right"><i class="icon-edit"></i></a>
    </p>
    <br/>
    <g:form class="form-horizontal" action="updateSchedule" id="${triggerName}">
        <div class="control-group">
            <label for="triggerName" class="control-label">Trigger Name:</label>

            <div class="controls">
                <input name="triggerName" id="triggerName" type="text" disabled="" value="${triggerName}"/>
            </div>

            <g:if test="${isScriptJob}">
                <label for="scriptUrl" class="control-label">Script Url:</label>

                <div class="controls">
                    <input name="scriptUrl" id="scriptUrl" type="text" disabled="" value="${scriptUrl}"/>
                </div>
            </g:if>

            <label for="nextFireTime" class="control-label">Next Fire Time:</label>

            <div class="controls">
                <input name="nextFireTime" id="nextFireTime" type="text" disabled="" value="${nextFireTime}"/>
            </div>
            <label for="availableSchedules" class="control-label">Available Schedules:</label>

            <div class="controls">
                <g:select name="availableSchedules" from="${availableSchedules}" value="${currentSchedule.toString()}"/>
                <input id="customCron" name="customCron" type="text" placeholder="* * * * ? *" value="${cron}"/>
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
