<%--
  Created by IntelliJ IDEA.
  User: tbarker
  Date: 7/24/12
  Time: 11:21 AM
  To change this template use File | Settings | File Templates.
--%>

<md:report>
    <div class="description">
        Change the security setting of reports by declaring them as anonymous, admin or neither.
        Apps that are not administrative or anonymous will use the default security
    </div>
    <md:header>Reports Grid</md:header>
    <div class="reportBody">
        <table class="basicReportTable">
            <tr>
                <th>Report Name</th>
                <th class="centeredTableHeader">Admin</th>
                <th class="centeredTableHeader">Anonymous</th>
                <th class="centeredTableHeader">Default</th>
            </tr>
            <g:each in="${reports}" var="report">
                <tr>
                    <td>${report.name}</td>

                    <td class="centeredRadioCell">
                        <g:if test="${report.isAdmin}">
                            <input type="radio" name="${report}" value="admin" checked="true"/>
                        </g:if>
                        <g:else>
                            <input type="radio" name="${report}" value="admin"/>
                        </g:else>
                    </td>
                    <td class="centeredRadioCell">
                        <g:if test="${report.isAnonymous}">
                            <input type="radio" name="${report}" value="anonymous" checked="true"/>
                        </g:if>
                        <g:else>
                            <input type="radio" name="${report}" value="anonymous"/>
                        </g:else>
                    </td>
                    <td class="centeredRadioCell">
                        <g:if test="${report.isDefault}">
                            <input type="radio" name="${report}" value="default" checked="true"/>
                        </g:if>
                        <g:else>
                            <input type="radio" name="${report}" value="default"/>
                        </g:else>
                    </td>
                </tr>
            </g:each>
        </table>
    </div>
</md:report>