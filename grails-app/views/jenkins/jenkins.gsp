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
<%--
  Created by IntelliJ IDEA.
  User: tbarker
  Date: 5/18/12
  Time: 1:22 PM
  To change this template use File | Settings | File Templates.
--%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Jenkins Admin Page</title>
    <r:require module="jenkins"/>
</head>

<body>

<div id="jenkinsDiv">

    <g:if test="${errorMessage == 'jenkinsRunning'}">
        <div id="runningJDiv">
            <h2>Jenkins is running ...</h2>
        </div>
    </g:if>
    <g:elseif test="${errorMessage == 'jenkinsNotRun'}">
        <div id="toRunJDiv">
            <g:form controller="jenkins" action="runJenkins" method="POST">
                <span class="buttons">
                    <a id="runJenkins" href="/metridoc-reports/jenkins/runJenkins">Run Jenkins</a>
                </span>
            </g:form>
        </div>
    </g:elseif>
    <g:else test="${errorMessage == 'noJenkins'}">
        <div id="downloadJDiv">
            <g:form controller="jenkins" action="download" method="POST">
                <span class="buttons">
                    <a id="downloadJenkins" href="/metridoc-reports/jenkins/download">Download Jenkins</a>
                </span>
            </g:form>
        </div>
    </g:else>

</div>

</body>
</html>