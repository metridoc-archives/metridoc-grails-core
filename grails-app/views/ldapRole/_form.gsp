<!--

    Copyright 2010 Trustees of the University of Pennsylvania Licensed under the
    Educational Community License, Version 2.0 (the "License"); you may
    not use this file except in compliance with the License. You may
    obtain a copy of the License at

    http://www.osedu.org/licenses/ECL-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on an "AS IS"
    BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
    or implied. See the License for the specific language governing
    permissions and limitations under the License.

-->
<%@ page import="metridoc.core.ShiroRole; metridoc.core.LdapRoleMapping" %>

<div class="fieldcontain ${hasErrors(bean: ldapRoleMappingInstance, field: 'name', 'error')} required">
    <label for="name">
        <g:message default="Group Name"/>
        <span class="required-indicator">*</span>
    </label>
    <g:textField name="name" required="" value="${ldapRoleMappingInstance?.name}"/>
</div>


<div class="fieldcontain ${hasErrors(bean: ldapRoleMappingInstance, field: 'roles', 'error')} ">
    <label for="roles">
        <g:message default="Roles"/>
    </label>
    <select name="roles" multiple="multiple" size="5">
        <g:each in="${ShiroRole.list()}" var="shiroRole">
            <g:if test="${ldapRoleMappingInstance?.roles?.contains(shiroRole)}">
                <option value="${shiroRole.name}" selected="selected">${shiroRole.name}</option>
            </g:if>
            <g:else>
                <option value="${shiroRole.name}">${shiroRole.name}</option>
            </g:else>
        </g:each>
    </select>
</div>

