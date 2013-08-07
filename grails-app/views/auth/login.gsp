<md:report module="login">
    <div id="template">

        <g:form action="signIn" class="form-horizontal">

            <input type="hidden" name="targetUri" value="${targetUri}"/>

            <div class="control-group">
                <label for="username">User Name :</label>

                <div class="controls">
                    <input type="text" id="username" name="username" value="${username}" placeholder="User Name"/>
                </div>
                <label for="password">Password :</label>

                <div class="controls inline-password-control">
                    <input type="password" id="password" name="password" value="" placeholder="Password"/>
                    <button type="submit" class="btn">
                        Sign In
                    </button>
                </div>
                <label for="rememberMe" id="rememberLabel">Remember Me? :</label>

                <div class="controls">
                    <g:checkBox name="rememberMe" value="${rememberMe}"/>
                </div>
            </div>
        </g:form>
    </div>
    <script>
        $(function () {
            $("#username").focus();
        });
    </script>
</md:report>