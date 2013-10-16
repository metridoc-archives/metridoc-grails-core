<div class="control-group">
    <label for="username" class="control-label">User Name</label>
    <g:set var="disableUserName" value="${disabled ? true : false}"></g:set>
    <div class="controls">
        <g:textField class="username" name="username" pattern=".{5,}" maxlength="250"
                     title="Username must be at least 5 characters in length" required=""
                     value="${shiroUserInstance?.username}" disabled="${disableUserName}" placeholder="User Name"/>
    </div>
</div>