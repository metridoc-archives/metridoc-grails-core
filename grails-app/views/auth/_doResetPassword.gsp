<div id="template">
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>

    <g:form method="post" action="doResetPassword">
        <div>
            <label for="password">
                <g:message code="shiroUser.password.label" default="Password"/>
                <span class="required-indicator">*</span>
            </label>
            <g:passwordField name="password"/>
        </div>

        <div>
            <label for="password">
                <g:message code="shiroUser.confirmPassword.label" default="Confirm Password"/>
                <span class="required-indicator">*</span>
            </label>
            <g:passwordField name="confirm"/>
        </div>

        <g:hiddenField name="resetPasswordId" value="${resetPasswordId}"/>

        <span class="buttons">
            <input type="submit" value="Update" id="upPasswordButton" name="submit"/>
        </span>
    </g:form>
</div>