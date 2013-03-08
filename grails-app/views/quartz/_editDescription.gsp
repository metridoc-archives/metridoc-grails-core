<div id="descriptionModal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="descriptionModal"
     aria-hidden="true">
    <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>

        <h3 id="descriptionModalLabel">Edit Description</h3>
    </div>
    <g:form action="saveDescription" id="${triggerName}">
        <g:hiddenField name="jobName" value="${triggerName}" />
        <div class="modal-body">

            <div class="control-group">
                <g:if test="${description != metridoc.core.QuartzController.NO_DESCRIPTION}">
                    <g:textArea name="description" value="${description}" />
                </g:if>
                <g:else>
                    <g:textArea name="description" />
                </g:else>
            </div>

        </div>

        <div class="modal-footer">
            <button class="btn" data-dismiss="modal" aria-hidden="true">Close</button>
            <button class="btn" type="submit" value="Save" id="saveDescriptionButton">Save changes</button>
        </div>
    </g:form>
</div>