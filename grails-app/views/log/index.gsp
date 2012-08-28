<!DOCTYPE html>

<%--
  Created by IntelliJ IDEA.
  User: dongheng
  Date: 8/23/12
  Time: 2:24 PM
  To change this template use File | Settings | File Templates.
--%>
<html>
<head>
    <r:require module="log"/>
    <r:layoutResources/>
</head>
<body>
<select id="typeSelector">
    <option value="all">All</option>
    <option value="error">Error</option>
    <option value="warn">Warn</option>
    <option value="info">Info</option>
</select>

${logResponse}

<r:layoutResources/>
</body>
</html>