<div>
    <label>Server</label>
    <g:textField style="width:750px" class="userInput" name="server" maxlength="750" value="${LDAP.server}"/>
</div>

<div>
    <label>Root DN</label>
    <g:textField style="width:750px" class="userInput" name="rootDN" maxlength="750" value="${LDAP.rootDN}"/>
</div>

<div>
    <label>User Search Base</label>
    <g:textField style="width:750px" class="userInput" name="userSearchBase" maxlength="750"
                 value="${LDAP.userSearchBase}"/>
</div>

<div>
    <label>User Search Filter</label>
    <g:textField style="width:750px" class="userInput" name="userSearchFilter" maxlength="750"
                 value="${LDAP.userSearchFilter}"/>
</div>

<div>
    <label>Group Search Base</label>
    <g:textField style="width:750px" class="userInput" name="groupSearchBase" maxlength="750"
                 value="${LDAP.groupSearchBase}"/>
</div>

<div>
    <label>Manager DN</label>
    <g:textField style="width:750px" class="userInput" name="managerDN" maxlength="750" value="${LDAP.managerDN}"/>
</div>

<div>
    <label>Manager Password</label>
    <g:passwordField name="managerPassword" id="${LDAP.managerPassword}" value="${LDAP.managerPassword}"
                     placeholder="Password"/>
</div>
