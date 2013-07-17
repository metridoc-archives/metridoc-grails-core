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
<g:each in="${categories}" var="category">
    <div class="linkContainer">
    <g:if test="${!category.adminOnly}">
        <div>
            <md:header>${category.name} <i class="${category.iconName}"></i>
                <a href="#" onclick="showApps(this.id)" class="categoryHeader">
                    <i class="icon-circle-arrow-down"></i>
                </a>

            </md:header>
        </div>

        <g:if test="${!applicationControllers}">
            <ul class="undecorated">
                <li>No applications available</li>
            </ul>
        </g:if>
        <div class="categoryDiv">
            <g:each in="${applicationControllers}" var="controller" status="i">
                <ul class="undecorated">
                    <li><a href="${createLink(url: controller.controllerPath)}">${controller.appName}</a><g:if
                            test="${controller.appDescription}">-</g:if>${controller.appDescription}</li>
                </ul>
            </g:each>
        </div>
        </div>
    </g:if>
    <g:else>

        <div class="linkContainer">
            <shiro:hasRole name="ROLE_ADMIN">
                <md:header>${category.name} <i class="${category.iconName}"></i>
                    <a href="#" onclick="showApps(this.id)" class="categoryHeader">
                        <i class="icon-circle-arrow-down"></i>
                    </a></md:header>
                <g:if test="${!adminControllers}">
                    <ul class="undecorated">
                        <li>No applications available</li>
                    </ul>
                </g:if>
                <div class="categoryDiv">
                    <g:each in="${adminControllers}" var="controller" status="i">
                        <ul class="undecorated">
                            <li><a href="${createLink(url: controller.controllerPath)}">${controller.appName}</a><g:if
                                    test="${controller.appDescription}">-</g:if>${controller.appDescription}</li>
                        </ul>
                    </g:each>
                </div>

            </shiro:hasRole>
        </div>

        </div>
    </g:else>
</g:each>
<script>
    var newID = "category"
    var newHeader = "header"
    var newIcon = "icon"
    $(function () {
        $('.categoryDiv').each(function (i) {
            $(this).attr({id: newID + i});
        });

        $('.categoryHeader').each(function (i) {
            $(this).attr({id: newHeader + i});
        });
        $('.icon-circle-arrow-down').each(function (i) {
            $(this).attr({id: newIcon + i});
        });


    });
    function showApps(id) {
        var targetID = id.replace("header", "category")
        var iconID = id.replace("header", "icon")
        $('#' + targetID).toggle()
        $('#' + iconID).toggleClass('icon-circle-arrow-down icon-circle-arrow-up')
    }

</script>
</body>

</html>
