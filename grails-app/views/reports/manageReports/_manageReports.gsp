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
<div class="md-application-content">
    <div class="description">
        Change the security setting of reports by declaring them as anonymous, admin or neither.
        Apps that are not administrative or anonymous will use the default security
    </div>

    <strong>
        Reports Grid
    </strong>
    <hr/>

    <div class="reportBody">
        <table class="basicReportTable">
            <tr>
                <th>Report Name</th>
                <th class="centeredTableHeader">Admin</th>
                <th class="centeredTableHeader">Anonymous</th>
            </tr>
            <g:each in="${reports}" var="report">
                <tr>
                    <td>${report}</td>
                    <td class="centeredRadioCell">
                        <input type="radio" name="${report}" value="admin"/>
                    </td>
                    <td class="centeredRadioCell">
                        <input type="radio" name="${report}" value="anonymous"/>
                    </td>
                </tr>
            </g:each>
        </table>
    </div>
</div>