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
<md:report>
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
            <g:if test="${!category.getKey().adminOnly}">
                <g:render template="listControllers" model="${[category: category]}"/>
            </g:if>
            <g:else>
                <shiro:hasRole name="ROLE_ADMIN">
                    <g:render template="listControllers" model="${[category: category]}"/>
                </shiro:hasRole>
            </g:else>
        </g:each>
    </body>
    </html>
</md:report>
