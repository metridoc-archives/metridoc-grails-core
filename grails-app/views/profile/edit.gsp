<%@ page import="metridoc.reports.ShiroUser" %>
<md:report>

    <div id="edit-shiroUser" class="content scaffold-edit" role="main">
        <h1>${shiroUserInstance?.username}</h1>
        <g:if test="${flash.message}">
            <div class="message" role="status">${flash.message}</div>
        </g:if>

        <g:hasErrors bean="${shiroUserInstance}">
            <ul class="errors" role="alert">
                <g:renderErrors bean="${shiroUserInstance}" as="list"/>
            </ul>
        </g:hasErrors>

        <g:if test="${shiroUserInstance.username != 'anonymous'}">

            <g:form method="post" class="form-horizontal">
                <g:hiddenField name="id" value="${shiroUserInstance?.id}"/>
                <g:hiddenField name="version" value="${shiroUserInstance?.version}"/>

                <div class="control-group">
                    <label class="control-label" for="emailAddress">Email Address</label>

                    <div class="controls">
                        <g:textField name="emailAddress" required="" placeholder="foo@bar.com"
                                     value="${shiroUserInstance?.emailAddress}"/>
                    </div>
                    <label class="control-label" for="roles">Roles</label>

                    <div class="controls" id="shiroRoles">
                        <g:each in="${shiroUserInstance.roles}" var="shiroRole">
                            <span aria-labelledby="roles-label">${shiroRole.name}</span>
                        </g:each>
                    </div>
                    <label class="control-label" for="changePW">Change Password?</label>

                    <div class="controls" id="ifChangePassword">
                        <g:checkBox name="changePW" id="changePW" value="${false}"></g:checkBox>
                    </div>

                </div>

                <div class="control-group">

                    <label class="control-label changePassword" for="oldPassword">Current Password <span
                            class="required-indicator">*</span></label>
                    <div class="controls changePassword">
                        <g:passwordField name="oldPassword" placeholder="Password"/>
                    </div>

                    <label class="control-label changePassword" for="oldPassword">New Password <span
                            class="required-indicator">*</span></label>
                    <div class="controls changePassword">
                        <g:passwordField name="password" placeholder="Password"/>
                    </div>

                    <label class="control-label changePassword" for="oldPassword">Confirm Password <span
                            class="required-indicator">*</span></label>
                    <div class="controls changePassword">
                        <g:passwordField name="confirm" placeholder="Password"/>
                    </div>

                    <div class="controls input-prepend">
                        <button type="submit" class="btn" name="_action_update">
                            <i class="icon-edit"></i> Update
                        </button>
                    </div>

                </div>

            </g:form>
        </g:if>
    </div>
</md:report>