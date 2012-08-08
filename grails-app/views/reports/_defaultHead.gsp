<g:if test="${params.layout}">
    <meta name="layout" content="${layout}"/>
</g:if>
<g:else>
    <meta name="layout" content="main"/>
</g:else>

<g:if test="${hasModule}">
    <r:require module="${module}"/>
</g:if>

