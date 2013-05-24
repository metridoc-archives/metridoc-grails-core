<%--
  Created by IntelliJ IDEA.
  User: tbarker
  Date: 2/1/13
  Time: 2:13 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<md:report>
    <tmpl:manageReportHeaders/>
    <md:header>Edit Controller Security</md:header>
    <g:form class="form-horizontal" action="update">
        <g:hiddenField name="controllerName" value="${controllerDetails.controllerName}"/>
        <div class="control-group">
            <label for="controllerForSecurity" class="control-label">Controller Name:</label>

            <div class="controls">
                <g:textField name="controllerForSecurity" disabled="true" value="${controllerDetails.controllerName}"/>
            </div>
            <label for="isProtected" class="control-label">Protected?</label>

            <div class="controls">
                <g:checkBox name="isProtected" value="${controllerDetails.isProtected}"/>
            </div>
            <g:render template="/user/roles" model="[selectedRoles: controllerDetails.roles]"/>
            <div class="controls">
                <button class="btn" type="submit">
                    <i class="icon-edit"></i> Update
                </button>
            </div>
        </div>
    </g:form>
</md:report>