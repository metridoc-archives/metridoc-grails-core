
<md:saveModal action="saveDescription" id="${triggerName}" name="description" header="Edit Description">
    <div class="control-group">
        <g:hiddenField name="jobName" value="${triggerName}" />
        <g:if test="${description != metridoc.core.QuartzController.NO_DESCRIPTION}">
            <g:textArea name="description" value="${description}"/>
        </g:if>
        <g:else>
            <g:textArea name="description"/>
        </g:else>
    </div>
</md:saveModal>