<form id="quartz-table-form">
    %{--TODO: eventually integrate hidden method overrides, ie delete for deleting a job--}%
    %{--<input type="hidden" name="_method" id="quartzActionMethod">--}%
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
                <td>${job.jobName}</td>
                <td><a href="<g:createLink action="show" id="${job.trigger?.name}"/>">${job.trigger?.name}</a></td>
                <td class="quartz-tooltip quartz-status ${job.status ?: "not-run"}" data-toggle="tooltip"
                    data-original-title="${job.tooltip}">${job.lastRun}</td>
                <td class="quartz-to-hide">${job.tooltip}</td>
                <g:if test="${scheduler.isInStandbyMode() || job.triggerStatus == org.quartz.Trigger.TriggerState.PAUSED}">
                    <td class="hasCountdown countdown_amount">Paused</td>
                </g:if>
                <g:elseif test="${job.interrupting}">
                    <td data-last-runtime="${job.duration}">Interupting  <i class="icon-spinner icon-spin"></i>
                    </td>
                </g:elseif>
                <g:elseif test="${job.status == "running"}">
                    <td data-last-runtime="${job.duration}">Running  <i class="icon-spinner icon-spin"></i>
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
                            <g:if test="${job.triggerStatus == org.quartz.Trigger.TriggerState.PAUSED}">
                                <tmpl:quartzActionLink action="resume" icon="icon-time" jobName="${job.jobKey.name}"/>
                            </g:if>
                            <g:elseif test="${job.trigger.mayFireAgain()}">
                                <tmpl:quartzActionLink action="pause" icon="icon-pause"
                                                       question="pause all triggers for ${job.jobKey.name}"
                                                       jobName="${job.jobKey.name}"/>
                                <tmpl:quartzActionLink action="runNow" icon="icon-play"
                                                       jobName="${job.trigger.key.name}"/>
                            </g:elseif>
                        </g:if>

                    </g:if>

                    <g:else>
                        <g:if test="${!job.interrupting}">
                            <tmpl:quartzActionLink action="stopJob" icon="icon-stop"
                                                   question="stop job ${job.trigger.key.name}"
                                                   jobName="${job.trigger.key.name}"/>
                        </g:if>
                    </g:else>
                    <g:if test="${job.scriptJob}">
                        <tmpl:quartzActionLink action="deleteJob" icon="icon-trash"
                                               question="delete job ${job.trigger.key.name}"
                                               jobName="${job.trigger.key.name}"/>
                    </g:if>
                </td>
            </tr>
        </g:each>
        </tbody>
    </table>
</form>