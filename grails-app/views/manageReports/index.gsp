<%--
  Created by IntelliJ IDEA.
  User: tbarker
  Date: 7/24/12
  Time: 11:21 AM
  To change this template use File | Settings | File Templates.
--%>

<md:report>

    <div class="description">
        <g:if test="${flash.messages}">
            <g:each in="${flash.messages}" var="msg">
                <div class="message" role="status">${msg}</div>
            </g:each>
        </g:if>
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

                            <select name="role_${report.name}">
                                <option value="${report.role}">${report.role}</option>
                                <g:each in="${roles}" var="role">
                                    <g:if test="${role != report.role}">
                                        <option value="${role}">${role}</option>
                                    </g:if>
                                </g:each>
                            </select>
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