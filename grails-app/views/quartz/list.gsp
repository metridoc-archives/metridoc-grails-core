<%--
  Created by IntelliJ IDEA.
  User: tbarker
  Date: 8/24/12
  Time: 11:23 AM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<md:report>
    <div class="nav" role="navigation">
        <ul>
            <li><g:link class="home" controller="home" action="index"><g:message code="default.home.label"
                                                                                 default="Home"/></g:link></li>
            <li><g:link class="log" controller="log" action="index"><g:message code="default.log.label"
                                                                               default="Log"/></g:link></li>

        </ul>
    </div>

    <br/>

    <div id="jobTable" />
    <script type="text/javascript">

        function updateTable() {
            $.get('jobListOnly', function (data) {
                $('#jobTable').html(data);
            })
        }

        setInterval(function () {
                    updateTable();
                }, 5000
        )

        updateTable();
    </script>
</md:report>