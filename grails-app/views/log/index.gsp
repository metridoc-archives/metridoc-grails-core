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
    <option value="hour" selected="true">Last Hour</option>
    <option value="sixHours">Last 6 Hours</option>
    <option value="twelveHours">Last 12 Hours</option>
    <option value="day">Last 24 Hours</option>
</select>

<md:outputLogFile filePath="${grailsApplication.config.metridoc.home + "/logs/metridoc.log"}"/>

<r:layoutResources/>
</body>
</html>