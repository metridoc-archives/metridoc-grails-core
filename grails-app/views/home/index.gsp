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
</head>

<body>

<div id="page-body" role="main" style="padding: 20px; padding-left: 0px">

    <h1>Welcome to MetriDoc</h1>

    <p>
        MetriDoc is an extendable platform to view and maintain library statistics and reports.  Please choose an
        application or report below.
    </p>

    <div class="linkContainer">
        <md:header>Available Applications <i class="icon-bar-chart"></i></md:header>

        <g:if test="${!applicationControllers}">
            <ul class="undecorated">
                <li>No applications available</li>
            </ul>
        </g:if>

        <g:each in="${applicationControllers}" var="appController" status="i">
            <ul class="undecorated">
                <li><a href="${createLink(controller: appController.controllerName, action: 'index')}">${appController.title}</a><g:if test="${appController.description}"> - </g:if>${appController.description}</li>
            </ul>
        </g:each>
    </div>

    <div class="linkContainer">
        <shiro:hasRole name="ROLE_ADMIN">
            <md:header>Administration <i class="icon-cog"></i> </md:header>
            <g:each in="${adminControllers}" var="controller" status="i">
                <ul class="undecorated">
                    <li>
                        <a href="${createLink(controller: controller.controllerName, action: 'index')}">${controller.title}</a><g:if test="${controller.description}"> - </g:if>${controller.description}
                    </li>
                </ul>
            </g:each>
        </shiro:hasRole>
    </div>
</div>
</body>
</html>
