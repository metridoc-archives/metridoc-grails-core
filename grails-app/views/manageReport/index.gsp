<%--
  Created by IntelliJ IDEA.
  User: tbarker
  Date: 1/30/13
  Time: 2:37 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<md:report>
    <tmpl:manageReportHeaders/>
    <strong>Controller Specific Security:</strong>
    <br>
    <input type="text" id="searchController" class="userInput" name="search" maxlength="100"
           placeholder="Filter Controllers"/>

    <div class="row-fluid">
        <div class="span8">
            <table id="controllerTable" class="table table-striped table-hover">
                <tr>
                    <th><input type="checkbox" name="selectAll"></th>
                    <th>Controller</th>
                    <th>Protected?</th>
                    <th>Has Roles?</th>
                </tr>
                <g:each in="${controllerDetails}" var="detail">
                    <tr>
                        <td><input type="checkbox" name="controllerNames" value="${detail.key}"></td>
                        <td><g:link action="show" params="[id: detail.key]">${detail.key}</g:link></td>

                        <td>
                            <g:if test="${detail.value.isProtected}">
                                <i class="icon-check"></i>
                            </g:if>
                            <g:else>
                                <i class="icon-check-empty"></i>
                            </g:else>
                        </td>

                        <td>
                            <g:if test="${detail.value.roles}">
                                <i class="icon-check"></i>
                            </g:if>
                            <g:else>
                                <i class="icon-check-empty"></i>
                            </g:else>
                        </td>
                    </tr>
                </g:each>
            </table>
        </div>

        <div class="span4">
            <md:header>Edit Controller Security</md:header>
            <g:form action="updateAll">
                <g:hiddenField id="controllerNames" name="controllerNames" value=""/>
                <div class="control-group">

                    <label for="isProtected" class="control-label">Protected?</label>

                    <div class="controls">
                        <input type="checkbox" id="isProtected" name="isProtected"/>
                    </div>
                    <g:render template="/user/roles" model="[selectedRoles: controllerDetails.roles]"/>
                    <div class="controls">
                        <button class="btn" type="submit" onmouseover="getControllerNames()">
                            <i class="icon-edit"></i> Update
                        </button>
                    </div>
                </div>
            </g:form>

        </div>
    </div>
    <script>
        function getControllerNames() {
            var cNames = [];
            var table = document.getElementById("controllerTable");
            var i, j;
            var k = 0;
            var cellText;

            var boxes = $('input[name="controllerNames"]');

            for (i = 1, j = table.rows.length; i < j; i++) {

                //alert(table.rows[i].cells[1].innerHTML)
                //if(table.rows[i].getElementsByName('selectController').checked){
                if (boxes[i - 1].checked) {

                    cellText = table.rows[i].cells[1].innerHTML.replace('<a href=\"/metridoc-core/manageReport/show/', "");
                    cellText = cellText.replace('</a>', "");
                    cellText = cellText.replace(/[a-zA-Z]*">/, "");
                    cNames.push(cellText);
                }

            }
            $('#controllerNames').val(cNames);

        }
    </script>

    <script>
        $(document).ready(function () {
            $('#searchController').keyup(function () {
                var searchText = $('#searchController').val();
                var cellText;
                var table = document.getElementById("controllerTable");
                var i, j;
                for (i = 1, j = table.rows.length; i < j; i++) {
                    cellText = table.rows[i].cells[1].innerHTML.replace('<a href=\"/metridoc-core/manageReport/show/', "");
                    cellText = cellText.replace('</a>', "");
                    cellText = cellText.replace(/[a-zA-Z]*">/, "");


                    if (cellText.indexOf(searchText) != -1) {
                        $('#controllerTable tr').slice(i, i + 1).show();
                    }
                    else {
                        $('#controllerTable tr').slice(i, i + 1).hide();
                    }


                }
            });
            $('input[name=selectAll]').click(function () {
                if (this.checked) {
                    $('input[name=controllerNames]').prop("checked", true);

                }
                else {
                    $('input[name=controllerNames]').prop("checked", false);

                }
            });

        })
    </script>
</md:report>