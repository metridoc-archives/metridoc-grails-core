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
        <g:form action="updateReportSecurity" name="updateSecurityReportForm">
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
                        <tmpl:radioItem reportName="${report}" typeTest="${report.isAdmin}" type="admin"/>
                        <tmpl:radioItem reportName="${report}" typeTest="${report.isAnonymous}" type="anonymous"/>
                        <tmpl:radioItem reportName="${report}" typeTest="${report.isDefault}" type="default"/>
                    </tr>
                </g:each>
            </table>

            <div id="submitButton">
                <span class="buttons">
                    <a id="updateReportSecurity" href="#">Update Security</a>
                </span>
                <span class="buttons">
                    <a id="refreshReportSecurity" href="#">Cancel</a>
                </span>
            </div>
        </g:form>
    </div>
</md:report>