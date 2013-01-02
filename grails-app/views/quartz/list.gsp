<%--
  Created by IntelliJ IDEA.
  User: tbarker
  Date: 8/24/12
  Time: 11:23 AM
  To change this template use File | Settings | File Templates.
--%>

<%@ page import="org.quartz.Trigger" contentType="text/html;charset=UTF-8" %>
<md:report>
    <g:if test="${!emailIsConfigured}">
        <div class="ui-widget md-email-alert">
            <div class="ui-state-error ui-corner-all">
                <p>
                    <span class="ui-icon ui-icon-alert"></span>Email has not been set up properly, no notifications will be sent on job failures
                </p>
            </div>
        </div>
    </g:if>
    <g:if test="${badEmailMessage}">
        <div class="ui-widget md-email-alert">
            <div class="ui-state-error ui-corner-all">
                <p>
                    <span class="ui-icon ui-icon-alert"></span>${badEmailMessage}
                </p>
            </div>
        </div>
    </g:if>
    <g:render template="quartzSettings" plugin="metridoc-core"/>
    <div class="body">
        <h1 id="quartz-title">
            Quartz Jobs
            <span id="quartz-actions">
                <g:if test="${scheduler.isInStandbyMode()}">
                    <a href="<g:createLink action="startScheduler"/>"><img class="quartz-tooltip"
                                                                           data-tooltip="Start scheduler"
                                                                           src="<g:resource dir="quartz/images"
                                                                                            file="play-all.png"
                                                                                            plugin="metridoc-core"/>">
                    </a>
                </g:if>
                <g:else>
                    <a href="<g:createLink action="stopScheduler"/>"><img class="quartz-tooltip"
                                                                          data-tooltip="Pause scheduler"
                                                                          src="<g:resource dir="quartz/images"
                                                                                           file="pause-all.png"
                                                                                           plugin="metridoc-core"/>">
                    </a>
                </g:else>
                <a id="quartz-settings" class="quartz-tooltip" data-tooltip="Quartz settings" href="#"
                   style="padding-left: 32px; padding-top: 10px">
                    <r:img style="padding-top: 5px" dir="images/skin" file="applications-system.png"
                           plugin="metridoc-core"/>
                </a>

            </span>
        </h1>

        <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
        </g:if>
        <div id="clock" data-time="${now.time}">
            <h3>Current Time: ${now}</h3>
        </div>

        <div class="list">
            <table id="quartz-jobs">
                <thead>
                <tr>
                    <th>Name</th>
                    <th>Trigger Name</th>
                    <th>Last Run</th>
                    <th class="quartz-to-hide">Result</th>
                    <th>Next Scheduled Run</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                <g:each in="${jobs}" status="i" var="job">
                    <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        <td>${job.name}</td>
                        <g:if test="${job.trigger?.key?.name?.startsWith("manual")}">
                            <td>Manual Job</td>
                        </g:if>
                        <g:else>
                            <td>${job.trigger?.name}</td>
                        </g:else>
                        <td class="quartz-tooltip quartz-status ${job.status ?: "not-run"}"
                            data-tooltip="${job.tooltip}">${job.lastRun}</td>
                        <td class="quartz-to-hide">${job.tooltip}</td>
                        <g:if test="${scheduler.isInStandbyMode() || job.triggerStatus == Trigger.TriggerState.PAUSED}">
                            <td class="hasCountdown countdown_amount">Paused</td>
                        </g:if>
                        <g:elseif test="${job.status == "running"}">
                            <td>Running</td>
                        </g:elseif>
                        <g:elseif test="${job.trigger?.key?.name?.startsWith("manual")}">
                            <td>NA</td>
                        </g:elseif>
                        <g:else>
                            <td class="quartz-countdown"
                                data-next-run="${job.trigger?.nextFireTime?.time ?: ""}">${job.trigger?.nextFireTime}</td>
                        </g:else>
                        <td class="quartz-actions">
                            <g:if test="${job.status != 'running'}">
                                <g:if test="${job.trigger}">
                                    <g:if test="${job.triggerStatus == Trigger.TriggerState.PAUSED}">
                                        <a href="<g:createLink action="resume"
                                                               params="[jobName: job.name, jobGroup: job.group]"/>"><img
                                                class="quartz-tooltip" data-tooltip="Resume job schedule"
                                                src="<g:resource dir="quartz/images" file="resume.png"
                                                                 plugin="metridoc-core"/>"></a>
                                    </g:if>
                                    <g:elseif test="${job.trigger.mayFireAgain()}">
                                        <a href="<g:createLink action="pause"
                                                               params="[jobName: job.name, jobGroup: job.group]"/>"><img
                                                class="quartz-tooltip" data-tooltip="Pause job schedule"
                                                src="<g:resource dir="quartz/images" file="pause.png"
                                                                 plugin="metridoc-core"/>"></a>
                                    </g:elseif>
                                </g:if>
                                <g:else>
                                    <a href="<g:createLink action="start"
                                                           params="[jobName: job.name, jobGroup: job.group]"/>"><img
                                            class="quartz-tooltip" data-tooltip="Start job schedule"
                                            src="<g:resource dir="quartz/images" file="start.png"
                                                             plugin="metridoc-core"/>">
                                    </a>
                                </g:else>
                                <a href="<g:createLink action="runNow"
                                                       params="[jobName: job.name, jobGroup: job.group, triggerName: job.trigger?.key?.name, triggerGroup: job.trigger?.key?.group]"/>"><img
                                        class="quartz-tooltip" data-tooltip="Run now"
                                        src="<g:resource dir="quartz/images" file="run.png" plugin="metridoc-core"/>">
                                </a>
                            </g:if>
                        </td>
                    </tr>
                </g:each>
                </tbody>
            </table>
        </div>
    </div>
</md:report>