<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <g:render template="/reports/defaultHead" plugin="metridoc-core"/>
    </head>

    <body>
        <div class="md-application-content">
            <g:render template="/reports/defaultDescription" plugin="metridoc-core"/>
            ${body()}
        </div>
    </body>
</html>
