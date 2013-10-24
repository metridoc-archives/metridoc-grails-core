%{--
  - Copyright 2013 Trustees of the University of Pennsylvania. Licensed under the
  - 	Educational Community License, Version 2.0 (the "License"); you may
  - 	not use this file except in compliance with the License. You may
  - 	obtain a copy of the License at
  - 
  - http://www.osedu.org/licenses/ECL-2.0
  - 
  - 	Unless required by applicable law or agreed to in writing,
  - 	software distributed under the License is distributed on an "AS IS"
  - 	BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
  - 	or implied. See the License for the specific language governing
  - 	permissions and limitations under the License.  --}%

<%--
  Created by IntelliJ IDEA.
  User: intern
  Date: 8/1/13
  Time: 11:58 AM
  To change this template use File | Settings | File Templates.
--%>

<div class="md-application-content">
    <strong>LDAP Settings</strong>
    <g:form controller="LdapSettings">
        <g:hiddenField id="encryptStrong" name="encryptStrong" value="${LDAP.encryptStrong}"/>


        <fieldset class="form">
            <g:render template="/ldapSettings/form"/>
        </fieldset>


        <fieldset class="buttons">
            <g:actionSubmit action="save" name="save" class="btn btn-success"
                            value="Save"/>
        </fieldset>
    </g:form>
</div>

