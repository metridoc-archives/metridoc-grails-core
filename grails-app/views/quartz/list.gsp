<%--
  Created by IntelliJ IDEA.
  User: tbarker
  Date: 8/24/12
  Time: 11:23 AM
  To change this template use File | Settings | File Templates.
--%>

<md:report>
    <tmpl:quartzSettings/>
    <tmpl:newJob/>
    <div class="body">
        <h1 id="quartz-title">
            Quartz Jobs
            <span id="quartz-actions">
                <g:if test="${scheduler.isInStandbyMode()}">
                    <a id="start-scheduler" data-toggle="tooltip" data-original-title="Start scheduler" href="<g:createLink action="startScheduler"/>">
                        <i class="icon-play"></i>
                    </a>
                </g:if>
                <g:else>
                    <a id="stop-scheduler" data-toggle="tooltip" data-original-title="Pause scheduler" href="<g:createLink action="stopScheduler"/>">
                        <i class="icon-pause"></i>
                    </a>
                </g:else>
                <a id="quartz-settings" data-original-title="Quartz settings" href="#quartzModal"
                   data-toggle="modal">
                    <i class="icon-cog"></i>
                </a>
                <a id="new-job" data-toggle="modal" data-original-title="Add new job" href="#newJobModal">
                    <i class="icon-plus"></i>
                </a>

            </span>
        </h1>

        <div id="clock" data-time="${now.time}">
            <h3>Current Time: ${now}</h3>
        </div>

        <div id="jobListTable" class="list">
            <tmpl:jobTable/>
        </div>
    </div>
</md:report>