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
<strong>
    COUNTER Reports (<a href="#" id="counterLegend">legend</a>)
</strong>
<hr/>

<div id="counter-form">
    <g:if test="${yearList.size()>0}">
        <g:form controller="counter" action="downloadFile" method="GET">
            <div>
                <strong>Year:</strong>
                <SELECT name="year" id="year">
                    <g:each in="${yearList}" var="year">
                        <OPTION value="<%=year%>"><%=year%></OPTION>
                    </g:each>
                </SELECT>
            </div>

            <span id="filesOfYear">
                <g:each status="i" in="${fileTypeList}" var="fileType">
                    <g:if test="${i}">
                        <div>
                     <input type='radio' name='counter_group' value='${fileType.getKey()}'>
                        ${fileType.getValue()}
                        </input>
                    </div>
                    </g:if>
                    <g:else>
                        <div>
                     <input type='radio' name='counter_group' value='${fileType.getKey()}' checked="checked">
                        ${fileType.getValue()}
                        </input>
                    </div>
                    </g:else>
                </g:each>
            </span>

            <div id="submitButton">
                <span class="buttons">
                    <a id="getCounterReport" href="#">Get Report</a>
                </span>
            </div>

        </g:form>
    </g:if>
    <g:else>
        <p>
            Sorry, but "${path}" does not exists or there is no file available under this directory.
        </p>
    </g:else>
</div>