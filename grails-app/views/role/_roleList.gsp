<div style="border-top: 1px solid #ddd"></div>

<div style="margin-left:25px">
    <h2 style="font-size:16px; color:#48802c">Create New Role
        <a href="#" onclick="showRoleForm()">
            <i id="createRole" class="icon-plus-sign"></i>
        </a>

    </h2>

    <div id="createRoleForm" hidden="true">
        <g:form class="form-horizontal" controller="role" action="save">
            <div class="control-group">
                <label class="control-label" for="rolename">Role Name</label>

                <div class="controls">
                    <input name="rolename" id="rolename" type="text" required placeholder="ROLE_FOO"/>
                </div>
            </div>
            <md:cgButton icon="icon-edit">Create</md:cgButton>
        </g:form>
    </div>


    <div id="list-shiroRole" class="content scaffold-list" role="main">

        <g:if test="${flash.message}">
            <div class="message" role="status">${flash.message}</div>
        </g:if>
        <table class="table table-striped table-hover">
            <thead>
            <tr>
                <g:sortableColumn property="name" title="Roles"/>

            </tr>

            </thead>
            <tbody>
            <g:each in="${shiroRoleInstanceList}" status="i" var="shiroRoleInstance">
                <tr>
                    <td>
                        <g:link controller="role" action="show"
                                id="${shiroRoleInstance.id}">${fieldValue(bean: shiroRoleInstance, field: "name")}
                        </g:link>
                    </td>

                </tr>
            </g:each>
            </tbody>
        </table>

        <g:if test="${showRolePagination}">
            <div class="pagination">
                <g:paginate total="${shiroRoleInstanceTotal}"/>
            </div>
        </g:if>

    </div>
</div>
