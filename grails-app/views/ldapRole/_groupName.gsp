<label for="name" class="control-label">Group Name</label>
<g:set var="disableName" value="${disabled ? true : false}"></g:set>
<div class="controls">
    <g:textField class="name" name="name" maxlength="250"
                 value="${ldapRoleMappingInstance?.name}" disabled="${disableName}" placeholder="Group Name"/>
</div>