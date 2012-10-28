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
<!doctype html>

<html>
<head>
    <meta name="layout" content="main"/>
    <link rel="stylesheet" type="text/css" href="http://yui.yahooapis.com/3.5.1/build/cssgrids/grids-min.css">

</head>

<body>

<div id="page-body" role="main" style="padding: 50px; padding-left: 0px">

    <h1>Welcome to MetriDoc</h1>

    <p>
        MetriDoc is an extendable platform to view and maintain library statistics and reports.
    </p>
    <table id="accessInfoTable" border="1">
        <tr>
            <th>Link</th>
            <th>Access</th>
            <th>Title</th>
            <th>Category</th>
            <th>Description</th>
        </tr>
        <script>
            function checkAndSetAccess(status) {
                var url = $('#linkUrl_' + status).text();
                if (url.search("checkAccess") == -1) {
                    if (url.search('\\?') == -1) {
                        url = url + "?"
                    }

                    url = url + "checkAccess"
                }

                var text = $.ajax({
                    url:url,
                    type:'GET',
                    cache:false,
                    async:false
                }).responseText;

                if (text.search("Remember Me?") == -1) {
                    $('#linkHasAccess_' + status).html('true');
                } else {
                    $('#linkHasAccess_' + status).html('false');
                }
            }

        </script>
        <g:each in="${controllers}" var="controller" status="i">

            <tr>
                <td id="linkUrl_${i}"><g:createLink controller="${controller.controllerName}" action="${controller.action}"/></td>
                <td id="linkHasAccess_${i}"></td>
                <td>${controller.title}</td>
                <td>${controller.category}</td>
                <td>${controller.description}</td>
                <script type="text/javascript">checkAndSetAccess(${i})</script>
            </tr>
        </g:each>
    </table>

</div>
</body>
</html>
