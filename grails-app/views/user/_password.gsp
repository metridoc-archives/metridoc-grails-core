<div class="control-group">
    <label for="${label}" class="control-label password">${passwordPrepend}Password <span
            class="required-indicator">*</span></label>
    <g:if test="${noValidation}">
        <div class="controls password">
            <g:passwordField id="${passwordId}" name="${label}" placeholder="Password"/></div>
    </g:if>
    <g:else>
        <div class="controls password">
            <g:passwordField id="${passwordId}" name="${label}" placeholder="Password" pattern=".{5,}"
                             title="Password must be at least 5 characters long" required=""/>
        </div>
    </g:else>
</div>