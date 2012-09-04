<div id="template">
    <g:form method="post" action="doResetPassword">
        <div class="fieldcontain ${hasErrors(bean: shiroUserInstance, field: 'password', 'error')} ">
            <label for="password">
                <g:message code="shiroUser.password.label" default="Password"/>
                <span class="required-indicator">*</span>
            </label>
            <g:passwordField name="password"/>
        </div>

        <div class="fieldcontain ${hasErrors(bean: shiroUserInstance, field: 'password', 'error')} ">
            <label for="password">
                <g:message code="shiroUser.confirmPassword.label" default="Confirm Password"/>
                <span class="required-indicator">*</span>
            </label>
            <g:passwordField name="confirm"/>
        </div>

        <g:hiddenField name="resetPasswordId" value="${resetPasswordId}"/>

        <span class="buttons">
            <input type="submit" value="Update" id="button" name="submit"/>
        </span>
    </g:form>
</div>