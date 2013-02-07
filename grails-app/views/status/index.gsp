<md:report>
    <g:render template="/user/tabs"/>
    <ul>
        <li><strong>Java Exec:&nbsp;&nbsp;</strong><code>${javaCommand}</code></li>
        <li><strong>Java VM Arguments:&nbsp;&nbsp;</strong><code>${javaVmArguments}</code></li>
        <li><strong>Main Command:&nbsp;&nbsp;</strong><code>${mainCommand}</code></li>
        <li><strong>Data source url:&nbsp;&nbsp;</strong><code>${dataSourceUrl}</code></li>
        <li><strong>Application Name:&nbsp;&nbsp;</strong><code>${applicationName}</code></li>
        <li>
            <strong>Shiro Filters:</strong>

            <div>
                <pre>${shiroFilters}</pre>
            </div>
        </li>
    </ul>
</md:report>