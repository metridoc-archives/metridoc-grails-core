<ul class="nav nav-tabs">
    <g:render template="/user/tabLabel"
              plugin="metridocCore"
              model="[controllerName: controllerName,
                      actionName: actionName,
                      linkController: 'user',
                      linkAction: 'list',
                      linkText: 'User List',
                      icon: 'icon-group']"/>
    <g:render
            template="/user/tabLabel"
            plugin="metridocCore"
            model="[controllerName: controllerName,
                    actionName: actionName,
                    linkController: 'user',
                    linkAction: 'create',
                    linkText: 'Create User',
                    icon: 'icon-user']"/>
    <g:render
            template="/user/tabLabel"
            plugin="metridocCore"
            model="[controllerName: controllerName,
                    actionName: actionName,
                    linkController: 'role',
                    linkAction: 'list',
                    linkText: 'Role List',
                    icon: 'icon-list-alt']"/>
    <g:render
            template="/user/tabLabel"
            plugin="metridocCore"
            model="[controllerName: controllerName,
                    actionName: actionName,
                    linkController: 'role',
                    linkAction: 'create',
                    linkText: 'Create Role',
                    icon: 'icon-edit']"/>
</ul>


