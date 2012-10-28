<%--
  Created by IntelliJ IDEA.
  User: tbarker
  Date: 10/27/12
  Time: 10:39 AM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <title></title>
        <r:require module="accessInfo"/>
        <r:layoutResources/>
    </head>

    <body>
        <table id="accessInfoTable" border="1">
            <tr>
                <th>Link</th>
                <th>Access</th>
                <th>Title</th>
                <th>Category</th>
                <th>Description</th>
            </tr>
            <g:each status="i" in="${links}" var="link">
                <tr>
                    <td id="linkUrl_${i}"><g:createLink controller="${link.controller}" action="index"/></td>
                    <td id="linkHasAccess_${i}"></td>
                    <td>${link.title}</td>
                    <td>${link.category}</td>
                    <td>${link.description}</td>
                    <script type="text/javascript">checkAndSetAccess(${i})</script>
                </tr>
            </g:each>
        </table>
        <r:layoutResources/>
    </body>

</html>