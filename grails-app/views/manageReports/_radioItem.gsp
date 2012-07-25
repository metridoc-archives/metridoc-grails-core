<td class="centeredRadioCell">
    <g:if test="${typeTest}">
        <input type="radio" name="${reportName}" value="${type}" checked="true"/>
    </g:if>
    <g:else>
        <input type="radio" name="${reportName}" value="${type}"/>
    </g:else>
</td>