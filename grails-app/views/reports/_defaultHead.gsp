<g:if test="${params.layout}">
    <meta name="layout" content="${params.layout}"/>
</g:if>
<g:else>
    <meta name="layout" content="main"/>
</g:else>

<md:logMsg>params</md:logMsg>

<g:if test="${params.hasModule}">
    <r:require module="${params.module}"/>
</g:if>

