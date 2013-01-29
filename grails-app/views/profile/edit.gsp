<%@ page import="metridoc.reports.ShiroUser" %>
<md:report>
        <g:if test="${shiroUserInstance.username != 'anonymous'}">

            <g:form method="post" action="update" class="form-horizontal">
                <g:hiddenField name="id" value="${shiroUserInstance?.id}"/>
                <g:hiddenField name="version" value="${shiroUserInstance?.version}"/>

                <div class="control-group">
                    <g:render template="/user/userName" plugin="metridocCore" model="[disabled: true]"/>
                    <g:render template="/user/email" plugin="metridocCore" />
                    <g:render template="/user/roles" plugin="metridocCore" model="[disabled: true]"/>
                    <label class="control-label" for="changePW">Change Password?</label>
                    <div class="controls" id="ifChangePassword">
                        <g:checkBox name="changePW" id="changePW" value="${false}"></g:checkBox>
                    </div>
                    <g:render template="/user/password" plugin="metridocCore" model="[noValidation: true, passwordId: 'oldPassword', label:'oldPassword', passwordPrepend: 'Old ']"/>
                    <g:render template="/user/passwords" plugin="metridocCore" model="[noValidation: true]"/>
                    <g:render template="/user/button" plugin="metridocCore"
                              model="[content:'Update',
                                      icon:'icon-edit']"
                    />
                </div>
            </g:form>
        </g:if>
</md:report>