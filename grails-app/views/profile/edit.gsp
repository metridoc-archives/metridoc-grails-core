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

            <g:form method="post">
                <g:hiddenField name="id" value="${shiroUserInstance?.id}"/>
                <g:hiddenField name="version" value="${shiroUserInstance?.version}"/>

                <div class="fieldcontain ${hasErrors(bean: shiroUserInstance, field: 'emailAddress', 'error')} ">
                    <label for="emailAddress">
                        <g:message code="shiroUser.emailAddress.label" default="Email Address"/>
                    </label>
                    <g:textField name="emailAddress" required="" value="${shiroUserInstance?.emailAddress}"/>
                </div>

                <div class="fieldcontain ${hasErrors(bean: shiroUserInstance, field: 'roles', 'error')} ">
                    <label for="roles">
                        <g:message code="shiroUser.roles.label" default="Roles"/>
                    </label>
                    <g:each in="${shiroUserInstance.roles}" var="shiroRole">
                        <span aria-labelledby="roles-label">${shiroRole.name}</span>
                    </g:each>
                </div>

                <div id="ifChangePassword" class="fieldcontain">
                    <label for="changePW">
                        <g:message code="shiroUser.ifChangePassword.label" default="Change Password?"/>
                    </label>
                    <g:checkBox name="changePW" id="changePW" value="${false}">Change Password?</g:checkBox>
                </div>

                <div id="changePassword">

                    <div class="fieldcontain ${hasErrors(bean: shiroUserInstance, field: 'password', 'error')} ">
                        <label for="oldPassword">
                            <g:message code="shiroUser.oldPassword.label" default="Password"/>
                            <span class="required-indicator">*</span>
                        </label>
                        <g:passwordField name="oldPassword"/>
                    </div>

                    <div class="fieldcontain ${hasErrors(bean: shiroUserInstance, field: 'password', 'error')} ">
                        <label for="password">
                            <g:message code="shiroUser.password.label" default="Password"/>
                            <span class="required-indicator">*</span>
                        </label>
                        <g:passwordField name="password"/>
                    </div>

                    <div class="fieldcontain ${hasErrors(bean: shiroUserInstance, field: 'password', 'error')} ">
                        <label for="password">
                            <g:message code="shiroUser.confirmPassword.label" default="Confirm Password"/>
                            <span class="required-indicator">*</span>
                        </label>
                        <g:passwordField name="confirm"/>
                    </div>
                </div>
                <fieldset class="buttons">
                    <g:actionSubmit class="save" action="update"
                                    value="${message(code: 'default.button.update.label', default: 'Update')}"/>
                </fieldset>

            </g:form>
        </g:if>
    </div>
</md:report>