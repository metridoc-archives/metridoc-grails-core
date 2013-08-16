<%@ page import="metridoc.core.ShiroRole" %>
<%@ page import="metridoc.core.ShiroUser" %>
<div style="border-top: 1px solid #ddd"></div>
<br>

<div style="margin-left:25px">
    <g:render template="/manageReport/manageReportHeaders"/>
    <strong>Controller Specific Security:</strong>
    <br>
    <input type="text" id="searchControllers" class="userInput" name="searchControllers" maxlength="100"
           placeholder="Filter Controllers" value="${searchFilter}"/>
    <select name="roleFilter" id="roleFilter" onchange="filterByRole()">
        <option value="">All Roles</option>
        <g:each in="${ShiroRole.list()}" var="shiroRole">
            <option value="${shiroRole.name}">${shiroRole.name}</option>

        </g:each>

    </select>

    <div class="row-fluid">
        <div class="span8">
            <table id="controllerTable" class="table table-striped table-hover">
                <tr>
                    <th><input type="checkbox" name="selectAll"></th>
                    <th>Controller</th>
                    <th>Protected?</th>
                    <th>Has Roles?</th>
                </tr>
                <g:each in="${controllerDetails}" var="detail">
                    <g:if test="${detail.key != "home" && detail.key != "logout" && detail.key != "profile" && detail.key != "auth"}">
                        <tr>
                            <td><input type="checkbox" name="controllerNames" value="${detail.key}"></td>
                            <td><g:link controller="manageReport" action="show"
                                        params="[id: detail.key]">${detail.key}</g:link></td>

                            <td>
                                <g:if test="${detail.value.isProtected}">
                                    <i class="icon-check"></i>
                                </g:if>
                                <g:else>
                                    <i class="icon-check-empty"></i>
                                </g:else>
                            </td>


                            <td>
                                <g:if test="${detail.value.roles}">
                                    <i class="icon-check"></i>
                                    <a href="#" name="popRoles" class="popRoles" rel="popover" data-trigger="hover"
                                       data-content="${detail.value.roles}"><i
                                            class="icon-eye-open"></i></a>
                                </g:if>
                                <g:else>
                                    <i class="icon-check-empty"></i>
                                </g:else>

                            </td>

                        </tr>
                    </g:if>
                </g:each>
            </table>
        </div>

        <div class="span4">
            <md:header>Edit Controller Security</md:header>
            <g:form controller="manageReport" action="updateAll">
                <g:hiddenField id="controllerNames" name="controllerNames" value=""/>
                <g:hiddenField id="searchFilter" name="searchFilter" value=""/>
                <div class="control-group">

                    <label for="isProtected" class="control-label">Protected?</label>

                    <div class="controls">
                        <input type="checkbox" id="isProtected" name="isProtected" hidden/>

                        <div class="btn-group" data-toggle="buttons-radio">
                            <button type="button" class="btn btn-primary" name="isProtected"
                                    onclick="protect()">Protected</button>
                            <button type="button" class="btn btn-primary active" name="unProtected"
                                    onclick="unProtect()">Unprotected</button>
                        </div>
                    </div>

                    <g:render template="/user/roles" model="[selectedRoles: controllerDetails.roles]"/>
                    <div class="controls">
                        <button class="btn" type="submit" onmouseover="getControllerNames()">
                            <i class="icon-edit"></i> Update
                        </button>
                    </div>
                </div>
            </g:form>

        </div>
    </div>
</div>