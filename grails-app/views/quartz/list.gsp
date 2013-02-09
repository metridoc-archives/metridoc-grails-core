<%--
  Created by IntelliJ IDEA.
  User: tbarker
  Date: 8/24/12
  Time: 11:23 AM
  To change this template use File | Settings | File Templates.
--%>

<%@ page import="org.quartz.Trigger" contentType="text/html;charset=UTF-8" %>
<md:report>
    <tmpl:quartzSettings/>
    <div class="body">
        <h1 id="quartz-title">
            Quartz Jobs
            <span id="quartz-actions">
                <g:if test="${scheduler.isInStandbyMode()}">
                    <a href="<g:createLink action="startScheduler"/>" class="quartz-tooltip"
                       data-tooltip="Start scheduler">
                        <i class="icon-play"></i>
                    </a>
                </g:if>
                <g:else>
                    <a href="<g:createLink action="stopScheduler"/>" class="quartz-tooltip"
                       data-tooltip="Pause scheduler">
                        <i class="icon-pause"></i>
                    </a>
                </g:else>
                <a id="quartz-settings" class="quartz-tooltip" data-tooltip="Quartz settings" href="#quartzModal"
                   data-toggle="modal">
                    <i class="icon-cog"></i>
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
            <table id="quartz-jobs" class="table table-hover table-striped">
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
                        <g:elseif test="${job.interrupting}">
                            <td data-last-runtime="${job.lastDuration}">Interupting  <i class="icon-spinner icon-spin"></i>
                            </td>
                        </g:elseif>
                        <g:elseif test="${job.status == "running"}">
                            <td data-last-runtime="${job.lastDuration}">Running  <i class="icon-spinner icon-spin"></i>
                            </td>
                        </g:elseif>
                        <g:elseif test="${job.manualJob}">
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
                                                               params="[jobName: job.name, jobGroup: job.group]"/>"
                                           class="quartz-tooltip"
                                           data-tooltip="Schedule Job">
                                            <i class="icon-time"></i>
                                        </a>
                                    </g:if>
                                    <g:elseif test="${job.trigger.mayFireAgain()}">
                                        <a href="<g:createLink action="pause"
                                                               params="[jobName: job.name, jobGroup: job.group]"/>"
                                           class="quartz-tooltip" data-tooltip="Unschedule Job">
                                            <i class="icon-pause"></i>
                                        </a>
                                    </g:elseif>
                                </g:if>
                                <a href="<g:createLink action="runNow" id="${job.trigger?.key?.name}"/>"
                                   class="quartz-tooltip" data-tooltip="Run now">
                                    <i class="icon-play"></i>
                                </a>
                            </g:if>

                            <g:else>
                                <g:if test="${!job.interrupting}">
                                    <a href="<g:createLink action="stopJob" id="${job.trigger?.key?.name}"/>"
                                       class="quartz-tooltip" data-tooltip="Stop Job">
                                        <i class="icon-stop"></i>
                                    </a>
                                </g:if>
                            </g:else>
                        </td>
                    </tr>
                </g:each>
                </tbody>
            </table>
        </div>
    </div>
</md:report>