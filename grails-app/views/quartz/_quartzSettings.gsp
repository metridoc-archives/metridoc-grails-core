<div id="dialog" title="Settings">
    <g:form action="saveSettings">
        <div>
            <div>Email on Failures (Does not work yet)</div>

            <div class="textContainer">
                <g:textArea name="emails">${notificationEmails}</g:textArea>
            </div>
            <fieldset class="buttons">
                <input type="submit" value="Save" id="saveSettingsButton" class="save"/>
            </fieldset>
        </div>
    </g:form>
</div>