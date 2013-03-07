<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <g:if test="${params.layout}">
            <meta name="layout" content="${layout}"/>
        </g:if>
        <g:else>
            <meta name="layout" content="main"/>
        </g:else>

        <g:if test="${hasModule}">
            <r:require module="${module}"/>
        </g:if>
        <g:else>
            <r:require module="${controllerName}" strict="false"/>
        </g:else>
    </head>

    <body>
        <div class="md-application-content">
            <g:if test="${descriptionExists}">
                <div class="description">
                    <g:if test="${pluginName}">
                        <g:render template="${descriptionTemplate}" plugin="${pluginName}"/>
                    </g:if>
                    <g:else>
                        <g:render template="${descriptionTemplate}"/>
                    </g:else>
                </div>
            </g:if>
            ${body()}
        </div>
    </body>
</html>
