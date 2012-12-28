<div id="dialog" title="Settings">
    <g:form action="saveSettings">
        <div>
            <div>Email on Failures (Does not work yet)</div>

            <div class="ui-widget, md-email-alert" id="emailErrorContainer">
                <div class="ui-state-error ui-corner-all">
                    <p>
                        <span class="ui-icon ui-icon-alert"></span>Email has not been set up properly, no notifications will be sent on job failures
                    </p>
                </div>
            </div>

            <div class="textContainer">
                <g:textArea name="emails"/>
            </div>
            <fieldset class="buttons">
                <input type="button" value="Save" id="saveSettingsButton" class="save" onclick="alert('hey')"/>
            </fieldset>
        </div>
    </g:form>
</div>