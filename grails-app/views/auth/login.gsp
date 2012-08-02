<!--

    Copyright 2010 Trustees of the University of Pennsylvania Licensed under the
    Educational Community License, Version 2.0 (the "License"); you may
    not use this file except in compliance with the License. You may
    obtain a copy of the License at

    http://www.osedu.org/licenses/ECL-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on an "AS IS"
    BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
    or implied. See the License for the specific language governing
    permissions and limitations under the License.

-->

%{--TODO: weizhuo - please convert authentication to the new way using yui--}%
%{--TODO: weizhuo - also make the button look better--}%
%{--TODO: weizhuo - add reasonable spacing between each line like it is in the old way--}%

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Login</title>
    <r:require module="login"/>
    %{--<link href="/metridoc-core/auth/css/login.css" rel="stylesheet" type="text/css">--}%
    <link rel="stylesheet" type="text/css" href="http://yui.yahooapis.com/3.5.1/build/cssgrids/grids-min.css">
</head>

<body>
<g:if test="${flash.message}">
    <div class="message">${flash.message}</div>
</g:if>
<div>
<g:form action="signIn">
    <input type="hidden" name="targetUri" value="${targetUri}"/>

%{--<div class="yui3-g">--}%
    <div>
        <label for="username">User Name :</label>
        <input type="text" name="username" value="${username}" class="userInput"/>
    </div>

    <div>
        <label for="password">Password :</label>
        <input type="password" name="password" value="" class="userInput"/>
    </div>

    <div>
        <label for="rememberMe">Remember Me? :</label>
        <g:checkBox name="rememberMe" value="${rememberMe}"/>
    </div>


    <div class="buttons">
        <label for="submit"></label>
        <input type="submit" value="Sign in" id="button" name="submit"/>
    </div>

</g:form>
</div>
</body>
</html>