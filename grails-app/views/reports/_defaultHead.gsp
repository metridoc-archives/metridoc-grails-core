<g:if test="${params.layout}">
    <meta name="layout" content="${params.layout}"/>
</g:if>
<g:else>
    <meta name="layout" content="main"/>
</g:else>

<r:require module="${params.controller}"/>