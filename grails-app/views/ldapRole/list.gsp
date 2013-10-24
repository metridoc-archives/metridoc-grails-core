<!--

  - Copyright 2013 Trustees of the University of Pennsylvania. Licensed under the
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
<%@ page contentType="text/html;charset=UTF-8" %>
<md:report>
    <g:hiddenField name="previousExpanded" id="previousExpanded" value="${previousExpanded}"/>
    <g:render template="/commonTemplates/tabs"/>

    <div style="border-bottom: 1px solid #ddd">
        <a href="#" onclick="changeIcon('groupList')">
            <h1 style="font-size:14px; color:black">LDAP Groups&nbsp
                <i id="cGroupList" data-toggle="collapse" data-target="#groupList" class="icon-circle-arrow-up"></i>
            </h1>
        </a>

        <span id="sGroupList" style="margin-left: 25px">Create and view LDAP groups</span>

        <div id="groupList" class="collapse in">
            <g:render template="/ldapRole/ldapGroupList"/>
        </div>
    </div>
    <br>

    <div style="border-bottom: 1px solid #ddd">
        <a href="#" onclick="changeIcon('ldapConfig')">
            <h1 style="font-size:14px; color:black">LDAP Config&nbsp
                <i id="cLdapConfig" data-toggle="collapse" data-target="#ldapConfig" class="icon-circle-arrow-down"></i>
            </h1>
        </a>

        <span id="sLdapConfig" style="margin-left: 25px">Change LDAP configuration</span>

        <div id="ldapConfig" class="collapse">
            <g:render template="/ldapSettings/ldapConfig"/>
        </div>
    </div>
    <br>

</md:report>
