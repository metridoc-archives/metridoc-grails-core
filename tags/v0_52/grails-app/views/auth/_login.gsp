<div id="template">

    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>

    <g:form action="signIn">

        <input type="hidden" name="targetUri" value="${targetUri}"/>

        <div>
            <label for="username">User Name :</label>
            <input type="text" name="username" value="${username}" id="usernameInput" class="userInput"            />
        </div>

        <div>
            <label for="password">Password :</label>
            <input type="password" name="password" value="" class="userInput" id="passwordInput"/>
            <span class="buttons">
                <input type="submit" value="Sign in" id="button" name="submit"/>
            </span>
        </div>

        <div>
            <span>
                <label for="rememberMe" id="rememberLabel">Remember Me? :</label>
                <g:checkBox name="rememberMe" value="${rememberMe}"/>
            </span>

            <g:if test="${forgotPassword}">
                <span id="forgetPW">
                    <g:link controller="auth" action="forgetPassword"
                            name="forgetPW">Forgot Password ?</g:link>
                </span>
            </g:if>
        </div>





    </g:form>
</div>