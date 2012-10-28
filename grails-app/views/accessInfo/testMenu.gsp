<%--
  Created by IntelliJ IDEA.
  User: tbarker
  Date: 10/28/12
  Time: 5:49 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <title></title>
    <r:require module="jquery"></r:require>
    <r:layoutResources/>

</head>
<body>
    <div id="content"></div>
<script type="text/javascript">
    function insert(data) {
        alert(data)
        alert(JQuery(data).find('#accessInfoTable').html())
        alert('hey there')
    }
    $.ajax({
        url:'/metridoc-core/accessInfo',
        type:'GET',
        cache:false,
        async:false,
        dataType: "html",
        success: insert
    })
</script>
</body>
</html>