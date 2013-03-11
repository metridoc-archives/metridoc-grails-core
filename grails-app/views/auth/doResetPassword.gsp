<md:report>
    <div id="template">
        <g:form method="post" action="doResetPassword">
            <div>
                <label for="password">
                    <g:message code="shiroUser.newPassword.label" default="New Password"/>
                    <span class="required-indicator">*</span>
                </label>
                <g:passwordField name="password" required=""/>
            </div>

            <div>
                <label for="password">
                    <g:message code="shiroUser.confirmPassword.label" default="Confirm Password"/>
                    <span class="required-indicator">*</span>
                </label>
                <g:passwordField name="confirm" required=""/>
            </div>

            <g:hiddenField name="resetPasswordId" value="${resetPasswordId}"/>

            <span class="buttons">
                <input type="submit" value="Update" id="upPasswordButton" name="submit"/>
            </span>
        </g:form>
    </div>
</md:report>