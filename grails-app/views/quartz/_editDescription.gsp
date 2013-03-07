<div id="descriptionModal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="descriptionModal"
     aria-hidden="true">
    <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>

        <h3 id="myModalLabel">Quartz Settings</h3>
    </div>
    <g:form action="saveSettings">
        <div class="modal-body">

            <div>
                <div>Email on Failures</div>

                <div class="textContainer">
                    <g:textArea name="emails">${notificationEmails}</g:textArea>
                </div>
            </div>

        </div>

        <div class="modal-footer">
            <button class="btn" data-dismiss="modal" aria-hidden="true">Close</button>
            <button class="btn" type="submit" value="Save" id="saveSettingsButton">Save changes</button>
        </div>
    </g:form>
</div>