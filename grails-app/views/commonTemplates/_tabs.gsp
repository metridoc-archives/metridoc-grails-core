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
                    linkController: 'LdapSettings',
                    linkAction: 'index',
                    linkText: 'LDAP Config',
                    icon: 'icon-sitemap']"/>

</ul>


