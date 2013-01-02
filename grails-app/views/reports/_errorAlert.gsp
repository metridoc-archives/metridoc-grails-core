<g:if test="${showAlertIf}">
    <div class="ui-widget ${alertClass}">
        <div class="ui-state-error ui-corner-all">
            <p>
                <span class="ui-icon ui-icon-alert"></span>${alertMessage}
            </p>
        </div>
    </div>
</g:if>