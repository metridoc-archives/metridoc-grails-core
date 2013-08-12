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

<%@ page import="metridoc.core.LdapRoleMapping" %>

<md:report>

    <g:render template="/user/tabs"/>

    <g:form class="form-horizontal"
            onsubmit="if(this.submitted == '_action_delete') return window.confirm('Are you sure you want to delete the group ${ldapRoleMappingInstance?.name}?'); return true;">
        <g:hiddenField name="id" value="${ldapRoleMappingInstance?.id}"/>
        <div class="control-group">
            <g:render template="/user/roles" model="[disabled: true]"/>
            <div class="controls">
                <g:render template="/user/embeddedButton"
                          model="[type: 'submit', action: '_action_edit', icon: 'icon-edit', content: 'Edit']"/>
                <g:if test="${ldapRoleMappingInstance != null && (!userGroups || !userGroups.contains(ldapRoleMappingInstance.name))}">
                    <g:render template="/user/embeddedButton"
                              model="[type: 'submit',
                                      action: '_action_delete',
                                      icon: 'icon-trash',
                                      content: 'Delete',
                                      buttonClass: 'btn-danger',
                                      onClick: 'return confirm(\'Are you sure you want to delete this group?\');']"/>
                </g:if>
            </div>
        </div>
    </g:form>
</md:report>
