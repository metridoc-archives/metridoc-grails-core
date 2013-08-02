<%--
  Created by IntelliJ IDEA.
  User: intern
  Date: 8/1/13
  Time: 11:58 AM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<md:report>
    <g:render template="/user/tabs"/>
    <div class="md-application-content">
        <strong>LDAP Settings</strong>
        <g:form controller="AdminLdap">
            <g:hiddenField id="encryptStrong" name="encryptStrong" value="${LDAP.encryptStrong}"/>


            <fieldset class="form">
                <tmpl:form/>
            </fieldset>


            <fieldset class="buttons">
                <g:actionSubmit action="save" name="save" class="btn btn-success"
                                value="Save"/>
            </fieldset>
        </g:form>
    </div>

</md:report>
