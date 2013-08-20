<label for="name" class="control-label">${category}</label>
<g:set var="disableName" value="${disabled ? true : false}"></g:set>
<g:if test="${required}">
    <span class="required-indicator">*</span>
</g:if>
<div class="controls">
    <g:textField class="name" name="name" maxlength="250"
                 value="${target?.name}" disabled="${disableName}" placeholder="${category}"/>
</div>