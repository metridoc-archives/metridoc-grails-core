<%@ page import="metridoc.core.ShiroUser" %>
<md:report>
    <g:if test="${shiroUserInstance.username != 'anonymous'}">

        <g:form method="post" action="update" class="form-horizontal">
            <g:hiddenField name="id" value="${shiroUserInstance?.id}"/>
            <g:hiddenField name="version" value="${shiroUserInstance?.version}"/>

            <div class="control-group">
                <g:render template="/user/userName" model="[disabled: true]"/>
                <g:render template="/user/email"/>
                <g:render template="/user/roles" model="[disabled: true, target: shiroUserInstance]"/>
                <label class="control-label" for="changePW">Change Password?</label>

                <div class="controls" id="ifChangePassword">
                    <%--suppress CheckTagEmptyBody --%>
                    <g:checkBox name="changePW" id="changePW" value="${false}"></g:checkBox>
                </div>
                <g:render template="/user/password"
                          model="[noValidation: true, passwordId: 'oldPassword', label: 'oldPassword', passwordPrepend: 'Old ']"/>
                <g:render template="/user/passwords" model="[noValidation: true]"/>
                <g:render template="/user/button"
                          model="[content: 'Update',
                                  icon: 'icon-edit']"/>
            </div>
        </g:form>
    </g:if>
</md:report>