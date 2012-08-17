<%@ page import="metridoc.reports.ShiroUser" %>
<md:report>

    <div id="edit-shiroUser" class="content scaffold-edit" role="main">
        <h1><g:message code="default.edit.label" args="['Account']" default="Edit Account"/></h1>
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

                <div class="fieldcontain ${hasErrors(bean: shiroUserInstance, field: 'username', 'error')} required">
                    <label for="username">
                        <g:message code="shiroUser.username.label" default="User Name"/>
                        <span class="required-indicator">*</span>
                    </label>
                    <span aria-labelledby="username-label">
                        <g:fieldValue bean="${shiroUserInstance}" field="username" name="username"/>
                    </span>
                </div>

                <div class="fieldcontain ${hasErrors(bean: shiroUserInstance, field: 'password', 'error')} ">
                    <label for="password">
                        <g:message code="shiroUser.password.label" default="Password"/>
                        <span class="required-indicator">*</span>
                    </label>
                    <g:passwordField name="password" required=""/>
                </div>

                <div class="fieldcontain ${hasErrors(bean: shiroUserInstance, field: 'password', 'error')} ">
                    <label for="confirm">
                        <g:message code="shiroUser.confirmPassword.label" default="Confirm Password"/>
                        <span class="required-indicator">*</span>
                    </label>
                    <g:passwordField name="confirm" required=""/>
                </div>

                <fieldset class="buttons">
                    <g:actionSubmit class="save" action="update"
                                    value="${message(code: 'default.button.update.label', default: 'Update')}"/>
                </fieldset>

            </g:form>
        </g:if>
    </div>
</md:report>