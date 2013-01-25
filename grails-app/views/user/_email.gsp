<label for="emailAddress" class="control-label">
    User Email <span class="required-indicator">*</span></label>
</label>
<div class="controls">
    <g:if test="${disabled}">
        <input name="emailAddress"
               required=""
               type="email"
               pattern=".{7,}"
               title="valid email is required, must be more than 7 characters"
               disabled="${disabled}"
               value="${shiroUserInstance?.emailAddress}" />
    </g:if>
    <g:else>
        <input name="emailAddress"
               required=""
               type="email"
               pattern=".{7,}"
               title="valid email is required, must be more than 7 characters"
               value="${shiroUserInstance?.emailAddress}" />
    </g:else>

</div>