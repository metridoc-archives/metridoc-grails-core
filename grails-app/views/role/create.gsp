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
<%@ page import="metridoc.core.ShiroRole" %>
<md:report>

    <g:render template="/user/tabs"/>

    <g:form class="form-horizontal" action="save">
        <div class="control-group">
            <label class="control-label" for="rolename">Role Name</label>

            <div class="controls">
                <input name="rolename" id="rolename" type="text" required placeholder="ROLE_FOO"/>
            </div>

            <div class="controls">
                <button class="btn" type="submit">
                    <i class="icon-edit"></i> Create
                </button>
            </div>
        </div>
    </g:form>
</md:report>

