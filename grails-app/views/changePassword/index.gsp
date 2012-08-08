<%@ page import="metridoc.reports.ShiroUser" %>
<md:report>

    <div id="edit-shiroUser" class="content scaffold-edit" role="main">
        <h1><g:message code="default.edit.label" args="['Account']" default="Edit Account"/></h1>
        <g:if test="${flash.message}">
            <div class="message" role="status">${flash.message}</div>
        </g:if>
        <g:hasErrors bean="${shiroUserInstance}">
            <ul class="errors" role="alert">
                <g:eachError bean="${shiroUserInstance}" var="error">
                    <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message
                            error="${error}"/></li>
                </g:eachError>
            </ul>
        </g:hasErrors>
        <g:form method="post">

            <g:hiddenField name="id" value="${shiroUserInstance?.id}"/>
            <g:hiddenField name="version" value="${shiroUserInstance?.version}"/>


            <div class="fieldcontain ${hasErrors(bean: shiroUserInstance, field: 'username', 'error')} required">
                <label for="username">
                    <g:message code="shiroUser.username.label" default="User Name"/>
                    <span class="required-indicator">*</span>
                </label>
                <span class="property-value" aria-labelledby="username-label"><g:fieldValue
                        bean="${shiroUserInstance}"
                        field="username"/></span>
            </div>

            <div class="fieldcontain ${hasErrors(bean: shiroUserInstance, field: 'password', 'error')} ">
                <label for="password">
                    <g:message code="shiroUser.passwordHash.label" default="Password"/>
                    <span class="required-indicator">*</span>
                </label>
                <g:passwordField name="password" required="" />
            </div>

            <div class="fieldcontain ${hasErrors(bean: shiroUserInstance, field: 'password', 'error')} ">
                <label for="password">
                    <g:message code="shiroUser.passwordHash.label" default="Confirm Password" />
                </label>
                <g:passwordField name="confirm" required="" />
            </div>

            <fieldset class="buttons">
                <g:actionSubmit class="save" action="update"
                                value="${message(code: 'default.button.update.label', default: 'Update')}"/>
                <g:actionSubmit class="cancel" action="cancel"
                                value="${message(code: 'default.button.cancel.label', default: 'Cancel')}" formnovalidate=""/>
            </fieldset>

        </g:form>
    </div>
</md:report>