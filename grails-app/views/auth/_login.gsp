<div id="template">

    <g:form action="signIn" class="form-horizontal">

        <input type="hidden" name="targetUri" value="${targetUri}"/>

        <div class="control-group">
            <label for="username">User Name :</label>

            <div class="controls">
                <input type="text" name="username" value="${username}" placeholder="User Name"/>
            </div>
            <label for="password">Password :</label>

            <div class="controls inline-password-control">
                <input type="password" name="password" value="" placeholder="Password"/>
                <button type="submit" class="btn">
                    Sign In
                </button>
            </div>
            <label for="rememberMe" id="rememberLabel">Remember Me? :</label>
            <div class="controls">
                <g:checkBox name="rememberMe" value="${rememberMe}"/>
                <g:if test="${forgotPassword}">
                    <span id="forgetPW">
                        <g:link controller="auth" action="forgetPassword"
                                name="forgetPW">Forgot Password ?</g:link>
                    </span>
                </g:if>
            </div>
        </div>
    </g:form>
</div>