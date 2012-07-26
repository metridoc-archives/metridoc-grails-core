<%--
  Created by IntelliJ IDEA.
  User: tbarker
  Date: 7/24/12
  Time: 11:21 AM
  To change this template use File | Settings | File Templates.
--%>

<md:report>
    <g:if test="${flash.message}">
        <div id="flashMessage">
            ${flash.message}
        </div>
    </g:if>
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
                    <th class="centeredTableHeader">Role</th>
                </tr>
                <g:each in="${reports}" var="report">
                    <tr>
                        <td>${report.displayName}</td>
                        <td class="centeredContent">
                            <input  type="text" value="${report.role}" name="role_${report.name}"/>
                        </td>
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