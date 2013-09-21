<%--
  Created by IntelliJ IDEA.
  User: intern
  Date: 8/14/13
  Time: 1:03 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page import="metridoc.core.ShiroRole" %>
<%@ page import="metridoc.core.ShiroUser" %>
<%@ page import="org.apache.shiro.SecurityUtils" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<md:report>
    <g:hiddenField name="previousExpanded" id="previousExpanded" value="${previousExpanded}"/>
    <g:render template="/commonTemplates/tabs"/>

    <div style="border-bottom: 1px solid #ddd">
        <a href="#" onclick="collapseOthers('userList')">
            <h1 style="font-size:14px; color:black">Manage Users&nbsp<i id="cUserList"
                                                                        class="icon-circle-arrow-down"></i></h1>

        </a>
        <span id="sUserList" style="margin-left: 25px">Create, view, and edit user info</span>

        <div id="userList" class="collapse">
            <g:render template="/user/userList"/>
        </div>
    </div>
    <br>

    <div style="border-bottom: 1px solid #ddd">
        <a href="#" onclick="collapseOthers('roleList')">
            <h1 style="font-size:14px; color:black">Manage Roles&nbsp<i id="cRoleList"
                                                                        class="icon-circle-arrow-down"></i></h1>

        </a>
        <span id="sRoleList" style="margin-left: 25px">Add roles</span>

        <div id="roleList" class="collapse">
            <g:render template="/role/manageRole"/>
        </div>
    </div>
    <br>

    <div style="border-bottom: 1px solid #ddd">
        <a href="#" onclick="collapseOthers('manageReportIndex')">
            <h1 style="font-size:14px; color:black">Manage Controllers&nbsp<i id="cManageReportIndex"
                                                                              class="icon-circle-arrow-down"></i></h1>

        </a>
        <span id="sManageReportIndex" style="margin-left: 25px">View and manage controller permissions</span>

        <div id="manageReportIndex" class="collapse">
            <g:render template="/manageReport/manageReportIndex"/>
        </div>
    </div>
    <br>
</md:report>