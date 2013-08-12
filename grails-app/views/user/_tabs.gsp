<ul class="nav nav-tabs">
    <g:render template="/user/tabLabel"
              model="[controllerName: controllerName,
                      actionName: actionName,
                      linkController: 'user',
                      linkAction: 'list',
                      linkText: 'User List',
                      icon: 'icon-group']"/>
    <!--
    <g:render
            template="/user/tabLabel"
            model="[controllerName: controllerName,
                    actionName: actionName,
                    linkController: 'user',
                    linkAction: 'create',
                    linkText: 'Create User',
                    icon: 'icon-user']"/>
    -->
    <g:render
            template="/user/tabLabel"
            model="[controllerName: controllerName,
                    actionName: actionName,
                    linkController: 'role',
                    linkAction: 'list',
                    linkText: 'Role List',
                    icon: 'icon-list-alt']"/>
    <!--
    <g:render
            template="/user/tabLabel"
            model="[controllerName: controllerName,
                    actionName: actionName,
                    linkController: 'role',
                    linkAction: 'create',
                    linkText: 'Create Role',
                    icon: 'icon-edit']"/>
    -->
    <g:render
            template="/user/tabLabel"
            model="[controllerName: controllerName,
                    actionName: actionName,
                    linkController: 'manageReport',
                    linkAction: 'index',
                    linkText: 'Report Security',
                    icon: 'icon-bar-chart']"/>
    <g:render
            template="/user/tabLabel"
            model="[controllerName: controllerName,
                    actionName: actionName,
                    linkController: 'status',
                    linkAction: 'index',
                    linkText: 'Status',
                    icon: 'icon-cogs']"/>
    <g:render
            template="/user/tabLabel"
            model="[controllerName: controllerName,
                    actionName: actionName,
                    linkController: 'restart',
                    linkAction: 'index',
                    linkText: 'Restart',
                    icon: 'icon-refresh']"/>
    <g:render
            template="/user/tabLabel"
            model="[controllerName: controllerName,
                    actionName: actionName,
                    linkController: 'manageConfig',
                    linkAction: 'index',
                    linkText: 'Config',
                    icon: 'icon-cog']"/>
    <g:render
            template="/user/tabLabel"
            model="[controllerName: controllerName,
                    actionName: actionName,
                    linkController: 'LdapSettings',
                    linkAction: 'index',
                    linkText: 'LDAP Config',
                    icon: 'icon-sitemap']"/>
    <g:render
            template="/user/tabLabel"
            model="[controllerName: controllerName,
                    actionName: actionName,
                    linkController: 'LdapRole',
                    linkAction: 'index',
                    linkText: 'LDAP Role Mapping',
                    icon: 'icon-table']"/>
</ul>


