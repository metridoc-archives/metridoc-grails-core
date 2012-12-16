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