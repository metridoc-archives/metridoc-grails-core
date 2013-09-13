<div style="border-top: 1px solid #ddd"></div>

<div style="margin-left:25px">
    <h2 style="font-size:16px; color:#48802c">Create New User
        <a href="#" onclick="showUserForm()">
            <i id="createUser" class="icon-plus-sign"></i>
        </a>

    </h2>

    <div id="createUserForm" hidden="true">
        <g:form controller="user" action="save" class="form-horizontal">
            <div class="control-group">
                <g:render template="/user/userName"/>
            </div>
            <g:render template="/user/passwords"/>
            <div class="control-group">
                <g:render template="/user/email"/>
            </div>
            <div class="control-group">
                <g:render template="/commonTemplates/roles"/>
            </div>

            <div class="control-group">
                <g:render template="/commonTemplates/button" model="['content': 'Create']" icon="icon-edit"/>
            </div>
        </g:form>
    </div>

    <div id="list-shiroUser" class="content scaffold-list" role="main">
        <table class="table table-striped table-hover">
            <thead>
            <tr>
                <g:sortableColumn property="username"
                                  title="Users"/>
                <th>Roles</th>
                <th></th>
            </tr>

            </thead>

            <tbody>
            <g:each in="${shiroUserInstanceList}" status="i" var="shiroUserInstance">
                <tr>
                    <td>
                        <g:link controller="user" action="show"
                                id="${shiroUserInstance.id}">${fieldValue(bean: shiroUserInstance, field: "username")}
                        </g:link>
                    </td>
                    <td>
                        <g:each in="${shiroUserInstance.roles}" var="role" status="count">

                            <g:if test="${count < 10}">
                                <span>
                                    <g:if test="${count < 9 && count < shiroUserInstance.roles.size() - 1}">
                                        ${role.name.minus("ROLE_").toLowerCase().capitalize()},&nbsp;
                                    </g:if>
                                    <g:else>
                                        ${role.name.minus("ROLE_").toLowerCase().capitalize()}
                                    </g:else>
                                </span>
                            </g:if>
                            <g:if test="${count == 10}">
                                <a href="#" name="popRoles" class="popRoles" rel="popover" data-trigger="hover"
                                   data-content="${shiroUserInstance.roles}" style="font:14">...</a>
                            </g:if>

                        </g:each>

                    </td>
                    <td>
                        <g:if test="${shiroUserInstance.username != 'anonymous'}">
                            <span class="inCellActions">

                                <g:link controller="user" action="edit"
                                        id="${shiroUserInstance.id}"><i class="icon-edit"></i>
                                </g:link>
                                <g:if test="${shiroUserInstance.username != currentUserName}">
                                    <a class="delete" href="#" onclick="deleteUser(${shiroUserInstance.id})">
                                        <i class="icon-trash"></i>
                                    </a>
                                </g:if>
                                <g:form name="mdForm_${shiroUserInstance.id}" method="delete" controller="user"
                                        action="delete"
                                        id="${shiroUserInstance.id}"/>

                            </span>
                        </g:if>
                    </td>
                </tr>
            </g:each>
            </tbody>
        </table>

        <g:if test="${showUserPagination}">
            <div class="pagination">
                <g:paginate total="${shiroUserInstanceTotal}"/>
            </div>
        </g:if>

    </div>
</div>