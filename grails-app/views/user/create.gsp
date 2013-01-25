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
<%@ page import="metridoc.reports.ShiroUser" %>
<md:report>

    <g:render template="/user/tabs" plugin="metridocCore"/>

    <g:form action="save" class="form-horizontal">
        <div class="control-group">
            <label for="username" class="control-label">
                User Name <span class="required-indicator">*</span>
            </label>

            <div class="controls">
                <g:textField pattern=".{5,}" title="User name required, must be at least 5 characters" name="username"
                             required="" value="${shiroUserInstance?.username}"/>
            </div>
            <label for="password" class="control-label">
                Password <span class="required-indicator">*</span>
            </label>

            <div class="controls">
                <g:passwordField pattern=".{5,}" title="Password must be at least 5 characters" name="password"
                                 required=""/>
            </div>
            <label for="confirm" class="control-label">
                Confirm Password <span class="required-indicator">*</span>
            </label>

            <div class="controls">
                <g:passwordField pattern=".{5,}" title="Password must be at least 5 characters" name="confirm"
                                 required=""/>
            </div>
            <label for="confirm" class="control-label">
                User Email <span class="required-indicator">*</span>
            </label>

            <div class="controls">
                <input required="" type="email" pattern=".{7,}" title="email must be valid and at least 7 characters"
                       name="emailAddress"/>
            </div>
            <label for="roles" class="control-label">
                Roles
            </label>

            <div class="controls">
                <select name="roles" multiple="multiple" size="5">
                    <g:each in="${metridoc.reports.ShiroRole.list()}" var="shiroRole">
                        <g:if test="${shiroUserInstance?.roles?.contains(shiroRole)}">
                            <option value="${shiroRole.name}" selected="selected">${shiroRole.name}</option>
                        </g:if>
                        <g:else>
                            <option value="${shiroRole.name}">${shiroRole.name}</option>
                        </g:else>
                    </g:each>
                </select>
            </div>
            <div class="controls">
                <button class="btn" type="submit">
                    <i class="icon-edit"></i> Create
                </button>
            </div>
        </div>
    </g:form>

%{--<div id="create-shiroUser" class="content scaffold-create" role="main">--}%
%{--<g:if test="${flash.message}">--}%
%{--<div class="message" role="status">${flash.message}</div>--}%
%{--</g:if>--}%

%{--<g:hasErrors bean="${shiroUserInstance}">--}%
%{--<ul class="errors" role="alert">--}%
%{--<g:renderErrors bean="${shiroUserInstance}" as="list"/>--}%
%{--</ul>--}%
%{--</g:hasErrors>--}%

%{--<g:form action="save">--}%
%{--<fieldset class="form">--}%
%{--<g:render template="form"/>--}%
%{--</fieldset>--}%
%{--<fieldset class="buttons">--}%
%{--<g:submitButton name="create" class="save"--}%
%{--value="${message(code: 'default.button.create.label', default: 'Create')}"/>--}%
%{--</fieldset>--}%
%{--</g:form>--}%

%{--</div>--}%

</md:report>

