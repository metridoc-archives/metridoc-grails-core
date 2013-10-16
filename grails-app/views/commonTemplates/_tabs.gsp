<ul class="nav nav-tabs">

    <g:render template="/commonTemplates/tabLabel"
              model="[controllerName: controllerName,
                      actionName: actionName,
                      linkController: 'manageAccess',
                      linkAction: 'list',
                      linkText: 'Manage Access',
                      icon: 'icon-group']"/>

    <g:render
            template="/commonTemplates/tabLabel"
            model="[controllerName: controllerName,
                    actionName: actionName,
                    linkController: 'manageConfig',
                    linkAction: 'index',
                    linkText: 'General Settings',
                    icon: 'icon-cog']"/>
    <g:render
            template="/commonTemplates/tabLabel"
            model="[controllerName: controllerName,
                    actionName: actionName,
                    linkController: 'ldapRole',
                    linkAction: 'list',
                    linkText: 'LDAP Config',
                    icon: 'icon-sitemap']"/>

</ul>


